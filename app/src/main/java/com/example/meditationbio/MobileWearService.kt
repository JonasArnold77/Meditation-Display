package com.example.meditationbio

import android.util.Log
import com.example.meditationbio.model.BloodPressureMeasurement
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.MeditationSession
import com.example.meditationbio.model.PostSessionQuestionnaire
import com.example.meditationbio.model.PreSessionQuestionnaire
import com.example.meditationbio.model.ProblemField
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MobileWearService : WearableListenerService() {

    companion object {
        private val staticClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .callTimeout(180, TimeUnit.SECONDS)
            .build()

        private const val MEDITATION_CONFIG_WEBHOOK_URL =
            "https://ingo-e-arnold.app.n8n.cloud/webhook-test/b74b3249-1912-4e2a-ad0b-f412a0644cb6"

        private const val SESSION_RESULT_WEBHOOK_URL =
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
                put("type", "meditation_config")
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

            MainActivity.setMeditationGenerating(true)
            MainActivity.setGeneratedMeditationText(null)
            MainActivity.showGeneratedMeditationDialog(false)
            MainActivity.updateSendStatus("Meditation wird an n8n gesendet...")

            postJson(
                url = MEDITATION_CONFIG_WEBHOOK_URL,
                json = json,
                onSuccess = { code, success, responseBody ->
                    MainActivity.setMeditationGenerating(false)

                    if (success) {
                        try {
                            val obj = JSONObject(responseBody)
                            val textContent = obj.optString("textContent", "")

                            if (textContent.isNotBlank()) {
                                MainActivity.setGeneratedMeditationText(textContent)
                                MainActivity.showGeneratedMeditationDialog(true)
                                MainActivity.updateSendStatus("Meditation erfolgreich empfangen.")
                            } else {
                                MainActivity.updateSendStatus(
                                    "Antwort erhalten, aber kein Meditationstext gefunden."
                                )
                            }
                        } catch (e: Exception) {
                            MainActivity.updateSendStatus(
                                "Antwort konnte nicht gelesen werden: ${e.message}"
                            )
                        }
                    } else {
                        MainActivity.updateSendStatus(
                            "Fehlerhafte Antwort von n8n. Code=$code, Body=$responseBody"
                        )
                    }
                },
                onError = { message ->
                    MainActivity.setMeditationGenerating(false)
                    MainActivity.updateSendStatus(
                        "Fehler beim Senden der Meditation: $message"
                    )
                }
            )
        }

        fun sendCompletedSessionToN8n(
            session: MeditationSession?,
            problemField: ProblemField?,
            recommendation: MeditationRecommendation?,
            preQuestionnaire: PreSessionQuestionnaire,
            postQuestionnaire: PostSessionQuestionnaire,
            bloodPressureBefore: BloodPressureMeasurement,
            bloodPressureAfter: BloodPressureMeasurement,
            latestPayload: String,
            liveBioText: String
        ) {
            val json = JSONObject().apply {
                put("type", "completed_session")

                put("session", JSONObject().apply {
                    put("session_id", session?.sessionId ?: "")
                    put("problem_field_id", session?.problemFieldId ?: "")
                    put("recommendation_id", session?.recommendationId ?: "")
                    put("recommendation_title", session?.recommendationTitle ?: "")
                    put("started_at_millis", session?.startedAtMillis ?: 0L)
                })

                put("problem_field", JSONObject().apply {
                    put("id", problemField?.id ?: "")
                    put("title", problemField?.title ?: "")
                    put("subtitle", problemField?.subtitle ?: "")
                    put("description", problemField?.description ?: "")
                })

                put("recommendation", JSONObject().apply {
                    put("id", recommendation?.id ?: "")
                    put("title", recommendation?.title ?: "")
                    put("subtitle", recommendation?.subtitle ?: "")
                    put("duration_minutes", recommendation?.durationMinutes ?: 0)
                    put("style", recommendation?.style ?: "")
                    put("tone", recommendation?.tone ?: "")
                    put("effectiveness_label", recommendation?.effectivenessLabel ?: "")
                })

                put("pre_questionnaire", JSONObject().apply {
                    put("emotional_load", preQuestionnaire.emotionalLoad)
                    put("inner_restlessness", preQuestionnaire.innerRestlessness)
                    put("overthinking", preQuestionnaire.overthinking)
                    put("openness_for_meditation", preQuestionnaire.opennessForMeditation)
                })

                put("post_questionnaire", JSONObject().apply {
                    put("calmness_now", postQuestionnaire.calmnessNow)
                    put("relief", postQuestionnaire.relief)
                    put("focus_now", postQuestionnaire.focusNow)
                    put("helpfulness", postQuestionnaire.helpfulness)
                    put("want_similar_meditations", postQuestionnaire.wantSimilarMeditations)
                })

                put("blood_pressure_before", JSONObject().apply {
                    put("systolic", bloodPressureBefore.systolic)
                    put("diastolic", bloodPressureBefore.diastolic)
                })

                put("blood_pressure_after", JSONObject().apply {
                    put("systolic", bloodPressureAfter.systolic)
                    put("diastolic", bloodPressureAfter.diastolic)
                })

                put("bio_feedback", JSONObject().apply {
                    put("live_bio_text", liveBioText)
                    put("latest_payload", latestPayload)
                })
            }

            postJson(
                url = SESSION_RESULT_WEBHOOK_URL,
                json = json,
                onSuccess = { code, success, _ ->
                    MainActivity.updateSendStatus(
                        "Session an n8n gesendet. Code=$code, success=$success"
                    )
                },
                onError = { message ->
                    MainActivity.updateSendStatus(
                        "Fehler beim Senden der Session: $message"
                    )
                }
            )
        }

        private fun postJson(
            url: String,
            json: JSONObject,
            onSuccess: (Int, Boolean, String) -> Unit,
            onError: (String) -> Unit
        ) {
            val body = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            Thread {
                try {
                    staticClient.newCall(request).execute().use { response ->
                        val responseBody = response.body?.string().orEmpty()
                        onSuccess(response.code, response.isSuccessful, responseBody)
                    }
                } catch (e: Exception) {
                    onError(e.message ?: "Unbekannter Fehler")
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

        MainActivity.updateLatestPayload(payload)

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

            MainActivity.updateLiveBioText(
                """
                $hrText
                $ibiText
                $accelText
                $gyroText
                """.trimIndent()
            )
        } catch (e: Exception) {
            MainActivity.updateLiveBioText(
                "Fehler beim Parsen der Bio-Daten: ${e.message}"
            )
        }
    }
}