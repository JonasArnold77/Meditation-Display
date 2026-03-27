package com.example.meditationbio.model

data class UserPreferences(
    val preferredDurationMinutes: Int = 10,
    val preferredTone: String = "sanft und warm",
    val preferredStyle: String = "Atemmeditation",
    val musicEnabled: Boolean = true,
    val spiritualLanguage: Boolean = false
)