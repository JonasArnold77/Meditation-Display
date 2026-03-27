package com.example.meditationbio.data

import com.example.meditationbio.model.CompletedSessionRecord
import com.example.meditationbio.model.MeditationRecommendation

object RecommendationRepository {

    fun getForProblemField(
        problemFieldId: String,
        completedSessions: List<CompletedSessionRecord> = emptyList()
    ): List<MeditationRecommendation> {
        val base = listOf(
            MeditationRecommendation(
                id = "stress_1",
                problemFieldId = "stress",
                title = "Sanfte Atemberuhigung",
                subtitle = "Hilft oft bei akuter Anspannung",
                durationMinutes = 10,
                style = "Atemmeditation",
                tone = "sanft und warm",
                effectivenessLabel = "hoch"
            ),
            MeditationRecommendation(
                id = "stress_2",
                problemFieldId = "stress",
                title = "Körper entspannen in 8 Minuten",
                subtitle = "Kurze Entlastung für zwischendurch",
                durationMinutes = 8,
                style = "Body Scan",
                tone = "ruhig",
                effectivenessLabel = "mittel"
            ),
            MeditationRecommendation(
                id = "anxiety_1",
                problemFieldId = "anxiety",
                title = "Erdung bei innerer Unruhe",
                subtitle = "Fokus auf Sicherheit und Stabilität",
                durationMinutes = 12,
                style = "Grounding",
                tone = "stabilisierend",
                effectivenessLabel = "hoch"
            ),
            MeditationRecommendation(
                id = "sleep_1",
                problemFieldId = "sleep",
                title = "Einschlafmeditation",
                subtitle = "Langsam herunterfahren am Abend",
                durationMinutes = 15,
                style = "Schlafmeditation",
                tone = "weich",
                effectivenessLabel = "hoch"
            ),
            MeditationRecommendation(
                id = "overthinking_1",
                problemFieldId = "overthinking",
                title = "Gedanken beruhigen",
                subtitle = "Weniger Kreisen, mehr innere Ruhe",
                durationMinutes = 10,
                style = "Achtsamkeit",
                tone = "klar und ruhig",
                effectivenessLabel = "mittel"
            ),
            MeditationRecommendation(
                id = "heartbreak_1",
                problemFieldId = "heartbreak",
                title = "Sanfte Selbstmitgefühls-Meditation",
                subtitle = "Trost und Wärme bei emotionalem Schmerz",
                durationMinutes = 12,
                style = "Selbstmitgefühl",
                tone = "warm",
                effectivenessLabel = "hoch"
            ),
            MeditationRecommendation(
                id = "selfworth_1",
                problemFieldId = "selfworth",
                title = "Innere Freundlichkeit stärken",
                subtitle = "Weniger Selbstkritik, mehr Annahme",
                durationMinutes = 10,
                style = "Selbstmitgefühl",
                tone = "ermutigend",
                effectivenessLabel = "mittel"
            )
        ).filter { it.problemFieldId == problemFieldId }

        return base
            .map { recommendation ->
                val matching = completedSessions.filter { it.recommendationId == recommendation.id }
                if (matching.isEmpty()) {
                    recommendation
                } else {
                    val avg = matching.map { it.effectivenessScore }.average().toInt()
                    recommendation.copy(
                        effectivenessLabel = labelFromScore(avg)
                    )
                }
            }
            .sortedByDescending { recommendation ->
                scoreFromLabel(recommendation.effectivenessLabel)
            }
    }

    private fun labelFromScore(score: Int): String {
        return when {
            score >= 75 -> "hoch"
            score >= 55 -> "mittel"
            else -> "niedrig"
        }
    }

    private fun scoreFromLabel(label: String): Int {
        return when (label) {
            "hoch" -> 3
            "mittel" -> 2
            else -> 1
        }
    }
}