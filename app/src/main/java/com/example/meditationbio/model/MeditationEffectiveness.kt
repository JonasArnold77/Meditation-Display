package com.example.meditationbio.model

data class MeditationEffectiveness(
    val score: Int,
    val subjectiveImprovement: Int,
    val bloodPressureDeltaSystolic: Int,
    val bloodPressureDeltaDiastolic: Int,
    val summary: String
)