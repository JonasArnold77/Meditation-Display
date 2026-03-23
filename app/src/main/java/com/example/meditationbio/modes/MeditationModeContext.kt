package com.example.meditationbio.modes

data class MeditationModeContext(
    val sessionId: String,
    val elapsedSec: Int,
    val lastAction: String? = null
)