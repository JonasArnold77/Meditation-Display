package com.example.meditationbio.logic

import com.example.meditationbio.model.BloodPressureMeasurement
import com.example.meditationbio.model.MeditationEffectiveness
import com.example.meditationbio.model.PostSessionQuestionnaire
import com.example.meditationbio.model.PreSessionQuestionnaire

object MeditationEffectivenessEvaluator {

    fun evaluate(
        pre: PreSessionQuestionnaire,
        post: PostSessionQuestionnaire,
        bpBefore: BloodPressureMeasurement,
        bpAfter: BloodPressureMeasurement
    ): MeditationEffectiveness {
        val preStressAverage = (
                pre.emotionalLoad +
                        pre.innerRestlessness +
                        pre.overthinking
                ) / 3.0

        val postReliefAverage = (
                post.calmnessNow +
                        post.relief +
                        post.focusNow +
                        post.helpfulness
                ) / 4.0

        val subjectiveImprovement = ((postReliefAverage - preStressAverage) * 10).toInt()

        val systolicDelta = bpAfter.systolic - bpBefore.systolic
        val diastolicDelta = bpAfter.diastolic - bpBefore.diastolic

        val bloodPressureScore = when {
            systolicDelta < 0 && diastolicDelta < 0 -> 15
            systolicDelta <= 0 || diastolicDelta <= 0 -> 8
            else -> 0
        }

        val similarBonus = if (post.wantSimilarMeditations) 5 else 0

        val rawScore = 50 + subjectiveImprovement + bloodPressureScore + similarBonus
        val finalScore = rawScore.coerceIn(0, 100)

        val summary = when {
            finalScore >= 75 -> "Diese Meditation war wahrscheinlich sehr hilfreich."
            finalScore >= 55 -> "Diese Meditation war wahrscheinlich hilfreich."
            finalScore >= 40 -> "Es gab eine leichte Wirkung."
            else -> "Es war nur wenig Veränderung erkennbar."
        }

        return MeditationEffectiveness(
            score = finalScore,
            subjectiveImprovement = subjectiveImprovement,
            bloodPressureDeltaSystolic = systolicDelta,
            bloodPressureDeltaDiastolic = diastolicDelta,
            summary = summary
        )
    }
}