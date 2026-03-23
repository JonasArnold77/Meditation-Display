package com.example.meditationbio.core

import com.example.meditationbio.features.MeditationFeatures
import com.example.meditationbio.modes.MeditationMode
import com.example.meditationbio.modes.MeditationModeContext
import com.example.meditationbio.modes.ModeResponse
import com.example.meditationbio.state.MeditationState

class MeditationDirector(
    private val activeMode: MeditationMode
) {
    fun onSessionStart(): ModeResponse {
        return activeMode.onSessionStart()
    }

    fun onSessionStop(): ModeResponse {
        return activeMode.onSessionStop()
    }

    fun decide(
        features: MeditationFeatures,
        state: MeditationState,
        sessionId: String,
        elapsedSec: Int
    ): ModeResponse {
        return activeMode.decide(
            features = features,
            state = state,
            context = MeditationModeContext(
                sessionId = sessionId,
                elapsedSec = elapsedSec
            )
        )
    }
}