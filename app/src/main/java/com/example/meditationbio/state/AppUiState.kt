package com.example.meditationbio.state

import com.example.meditationbio.model.BloodPressureMeasurement
import com.example.meditationbio.model.CompletedSessionRecord
import com.example.meditationbio.model.MeditationEditorConfig
import com.example.meditationbio.model.MeditationEffectiveness
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.MeditationSession
import com.example.meditationbio.model.PostSessionQuestionnaire
import com.example.meditationbio.model.PreSessionQuestionnaire
import com.example.meditationbio.model.ProblemField
import com.example.meditationbio.model.UserPreferences
import com.example.meditationbio.ui.AppScreen

data class AppUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val selectedProblemField: ProblemField? = null,
    val selectedRecommendation: MeditationRecommendation? = null,
    val currentSession: MeditationSession? = null,
    val recommendations: List<MeditationRecommendation> = emptyList(),
    val completedSessions: List<CompletedSessionRecord> = emptyList(),
    val preSessionQuestionnaire: PreSessionQuestionnaire = PreSessionQuestionnaire(),
    val postSessionQuestionnaire: PostSessionQuestionnaire = PostSessionQuestionnaire(),
    val bloodPressureBefore: BloodPressureMeasurement = BloodPressureMeasurement(),
    val bloodPressureAfter: BloodPressureMeasurement = BloodPressureMeasurement(),
    val effectiveness: MeditationEffectiveness? = null,
    val userPreferences: UserPreferences = UserPreferences(),
    val editorConfig: MeditationEditorConfig = MeditationEditorConfig(),
    val latestPayload: String = "Noch keine Daten von der Uhr empfangen.",
    val liveBioText: String = "Noch keine Bio-Daten geparst.",
    val sendStatus: String = "Noch nichts an n8n gesendet."
)