package com.example.meditationbio

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MobileWearService : WearableListenerService() {

    companion object {
        private val staticClient = OkHttpClient()

        private const val MEDITATION_CONFIG_WEBHOOK_URL =
            "https://ingo-e-arnold.app.n8n.cloud/webhook-test/b74b3249-1912-4e2a-ad0b-f412a0644cb6"

        fun sendMeditationConfigToN8n(
            language: String,
            goal: String,
            durationMinutes: Int,
            experienceLevel: String,
            style: String,
            tone: String,
            targetAudience: String,
            spiritual: Boolean,
            context: String,
            focus: String,
            musicRecommendation: Boolean,
            specialNotes: String
        ) {
            val json = JSONObject().apply {
                put("language", language)
                put("goal", goal)
                put("duration_minutes", durationMinutes)
                put("experience_level", experienceLevel)
                put("style", style)
                put("tone", tone)
                put("target_audience", targetAudience)
                put("spiritual", spiritual)
                put("context", context)
                put("focus", focus)
                put("music_recommendation", musicRecommendation)
                put("special_notes", specialNotes)
            }

            val body = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(MEDITATION_CONFIG_WEBHOOK_URL)
                .post(body)
                .build()

            Thread {
                try {
                    staticClient.newCall(request).execute().use { response ->
                        MainActivity.sendStatus =
                            "Meditation an n8n gesendet. Code=${response.code}, success=${response.isSuccessful}"
                    }
                } catch (e: Exception) {
                    MainActivity.sendStatus =
                        "Fehler beim Senden der Meditation: ${e.message}"
                }
            }.start()
        }
    }

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
            updateLiveBioDisplay(payload)
        }
    }

    private fun updateLiveBioDisplay(jsonPayload: String) {
        try {
            val obj = JSONObject(jsonPayload)

            val accelerometer = obj.optJSONObject("accelerometer")
            val gyroscope = obj.optJSONObject("gyroscope")
            val heartRate = obj.optJSONObject("heartRate")

            val accelText = if (accelerometer != null) {
                "Accel: x=${accelerometer.optDouble("x", 0.0)}, y=${accelerometer.optDouble("y", 0.0)}, z=${accelerometer.optDouble("z", 0.0)}"
            } else {
                "Accel: -"
            }

            val gyroText = if (gyroscope != null) {
                "Gyro: x=${gyroscope.optDouble("x", 0.0)}, y=${gyroscope.optDouble("y", 0.0)}, z=${gyroscope.optDouble("z", 0.0)}"
            } else {
                "Gyro: -"
            }

            val hrText = if (heartRate != null) {
                "HR: bpm=${heartRate.optInt("bpm", 0)}, status=${heartRate.optInt("status", -1)}"
            } else {
                "HR: -"
            }

            val ibiText = if (heartRate != null && heartRate.has("ibiMs")) {
                "IBI: ${heartRate.optJSONArray("ibiMs")}"
            } else {
                "IBI: -"
            }

            MainActivity.liveBioText = """
                $hrText
                $ibiText
                $accelText
                $gyroText
            """.trimIndent()
        } catch (e: Exception) {
            MainActivity.liveBioText = "Fehler beim Parsen der Bio-Daten: ${e.message}"
        }
    }
}