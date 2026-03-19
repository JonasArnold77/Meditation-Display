package com.example.meditationbio

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MobileWearService : WearableListenerService() {

    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        Log.d("MobileWearService", "Service erstellt")
    }

    override fun onMessageReceived(event: MessageEvent) {
        Log.d("MobileWearService", "Nachricht empfangen: path=${event.path}")

        val payload = String(event.data, Charsets.UTF_8)
        Log.d("MobileWearService", "Payload: $payload")

        MainActivity.latestPayload = payload

        if (event.path == "/bio") {
            sendToN8n(payload)
        }
    }

    private fun sendToN8n(jsonPayload: String) {
        val webhookUrl = "https://ingo-e-arnold.app.n8n.cloud/webhook-test/b74b3249-1912-4e2a-ad0b-f412a0644cb6"

        val body = jsonPayload.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(webhookUrl)
            .post(body)
            .build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d(
                        "N8N",
                        "Webhook gesendet. Code=${response.code}, success=${response.isSuccessful}"
                    )
                }
            } catch (e: Exception) {
                Log.e("N8N", "Fehler beim Senden an n8n: ${e.message}", e)
            }
        }.start()
    }
}