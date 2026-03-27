package com.example.meditationbio.model

data class MeditationSession(
    val sessionId: String,
    val problemFieldId: String?,
    val recommendationId: String?,
    val recommendationTitle: String?,
    val startedAtMillis: Long,
    val isRunning: Boolean = true
)