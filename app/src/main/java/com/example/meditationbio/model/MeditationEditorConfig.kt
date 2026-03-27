package com.example.meditationbio.model

data class MeditationEditorConfig(
    val language: String = "de",
    val goal: String = "innere Ruhe",
    val durationMinutes: Int = 10,
    val experienceLevel: String = "Anfänger",
    val style: String = "Atemmeditation",
    val tone: String = "sanft und warm",
    val targetAudience: String = "Erwachsene",
    val spiritual: Boolean = false,
    val context: String = "abends",
    val focus: String = "Atmung",
    val musicRecommendation: Boolean = true,
    val specialNotes: String = "keine esoterische Sprache"
)