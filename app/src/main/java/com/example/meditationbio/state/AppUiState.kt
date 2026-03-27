package com.example.meditationbio.state

import com.example.meditationbio.model.MeditationEditorConfig
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.ProblemField
import com.example.meditationbio.ui.AppScreen

data class AppUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val selectedProblemField: ProblemField? = null,
    val recommendations: List<MeditationRecommendation> = emptyList(),
    val editorConfig: MeditationEditorConfig = MeditationEditorConfig(),
    val latestPayload: String = "Noch keine Daten von der Uhr empfangen.",
    val liveBioText: String = "Noch keine Bio-Daten geparst.",
    val sendStatus: String = "Noch nichts an n8n gesendet."
)