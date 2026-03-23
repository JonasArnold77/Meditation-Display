package com.example.meditationbio.state

import com.example.meditationbio.features.MeditationFeatures

class MeditationStateEngine {

    fun resolve(features: MeditationFeatures, elapsedSec: Int): MeditationState {
        if (features.dataQualityScore < 40) {
            return MeditationState.LOW_CONFIDENCE
        }

        if (features.motionScore > 2.5 || features.heartRateTrend30s > 4) {
            return MeditationState.OVERLOADED
        }

        if (elapsedSec < 45) {
            return MeditationState.ARRIVING
        }

        if (features.stillnessScore < 55) {
            return MeditationState.SETTLING
        }

        if (features.stabilityScore > 75 && features.rmssd > 25) {
            return MeditationState.DEEPENING
        }

        if (features.stillnessScore > 65) {
            return MeditationState.STABLE
        }

        return MeditationState.DRIFTING
    }
}