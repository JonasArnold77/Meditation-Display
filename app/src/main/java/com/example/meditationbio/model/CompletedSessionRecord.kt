package com.example.meditationbio.model

data class CompletedSessionRecord(
    val sessionId: String,
    val problemFieldId: String,
    val recommendationId: String,
    val recommendationTitle: String,
    val effectivenessScore: Int,
    val summary: String,
    val timestampMillis: Long
)