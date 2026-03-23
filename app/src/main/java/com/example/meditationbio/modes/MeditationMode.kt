package com.example.meditationbio.modes

import com.example.meditationbio.features.MeditationFeatures
import com.example.meditationbio.state.MeditationState

interface MeditationMode {
    val id: String

    fun onSessionStart(): ModeResponse

    fun onSessionStop(): ModeResponse

    fun decide(
        features: MeditationFeatures,
        state: MeditationState,
        context: MeditationModeContext
    ): ModeResponse
}