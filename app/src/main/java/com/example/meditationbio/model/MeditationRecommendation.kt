package com.example.meditationbio.model

data class MeditationRecommendation(
    val id: String,
    val problemFieldId: String,
    val title: String,
    val subtitle: String,
    val durationMinutes: Int,
    val style: String,
    val tone: String,
    val effectivenessLabel: String
)