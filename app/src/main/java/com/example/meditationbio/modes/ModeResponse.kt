package com.example.meditationbio.modes

import com.example.meditationbio.breath.BreathPattern

data class ModeResponse(
    val speakNow: Boolean = false,
    val spokenText: String? = null,
    val nextBreathPattern: BreathPattern? = null,
    val stateLabel: String? = null,
    val actionLabel: String? = null
)