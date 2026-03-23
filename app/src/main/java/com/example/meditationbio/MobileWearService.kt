package com.example.meditationbio

import android.util.Log
import com.example.meditationbio.core.AppRuntimeStore
import com.example.meditationbio.sensors.SensorSample
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

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
            parseAndStoreSample(payload)
            sendToN8n(payload)
        }
    }

    private fun parseAndStoreSample(jsonPayload: String) {
        try {
            val obj = JSONObject(jsonPayload)

            val accelerometer = obj.getJSONObject("accelerometer")
            val gyroscope = obj.getJSONObject("gyroscope")
            val heartRate = obj.getJSONObject("heartRate")

            val ibiMsArray = heartRate.optJSONArray("ibiMs")
            val ibiStatusArray = heartRate.optJSONArray("ibiStatus")

            val ibiMs = mutableListOf<Int>()
            val ibiStatus = mutableListOf<Int>()

            if (ibiMsArray != null) {
                for (i in 0 until ibiMsArray.length()) {
                    ibiMs.add(ibiMsArray.optInt(i))
                }
            }

            if (ibiStatusArray != null) {
                for (i in 0 until ibiStatusArray.length()) {
                    ibiStatus.add(ibiStatusArray.optInt(i))
                }
            }

            val sample = SensorSample(
                sessionId = obj.optString("sessionId", "unknown_session"),
                sampleIndex = obj.optInt("sampleIndex", 0),
                timestamp = obj.optLong("timestamp", System.currentTimeMillis()),

                accelX = accelerometer.optDouble("x", 0.0),
                accelY = accelerometer.optDouble("y", 0.0),
                accelZ = accelerometer.optDouble("z", 0.0),

                gyroX = gyroscope.optDouble("x", 0.0),
                gyroY = gyroscope.optDouble("y", 0.0),
                gyroZ = gyroscope.optDouble("z", 0.0),

                heartRateBpm = heartRate.optInt("bpm", 0),
                heartRateStatus = heartRate.optInt("status", -1),
                ibiMs = ibiMs,
                ibiStatus = ibiStatus
            )

            AppRuntimeStore.sensorBuffer.add(sample)
            Log.d("MobileWearService", "SensorSample gespeichert: ${sample.sessionId} #${sample.sampleIndex}")
        } catch (e: Exception) {
            Log.e("MobileWearService", "Fehler beim Parsen des SensorSample", e)
        }
    }

    private fun sendToN8n(jsonPayload: String) {
        val webhookUrl = "https://DEIN-N8N-SERVER/webhook/meditation"

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