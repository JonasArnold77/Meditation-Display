package com.example.meditationbio.model

data class PreSessionQuestionnaire(
    val emotionalLoad: Int = 5,
    val innerRestlessness: Int = 5,
    val overthinking: Int = 5,
    val opennessForMeditation: Int = 5
)