package com.example.meditationbio.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.meditationbio.model.BloodPressureMeasurement
import com.example.meditationbio.model.CompletedSessionRecord
import com.example.meditationbio.model.MeditationEffectiveness
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.MeditationSession
import com.example.meditationbio.model.PostSessionQuestionnaire
import com.example.meditationbio.model.PreSessionQuestionnaire
import com.example.meditationbio.model.ProblemField
import com.example.meditationbio.model.UserPreferences
import com.example.meditationbio.ui.AppScreen

object AppStateStore {
    var uiState by mutableStateOf(AppUiState())
        private set

    fun navigateTo(screen: AppScreen) {
        uiState = uiState.copy(currentScreen = screen)
    }

    fun selectProblemField(problemField: ProblemField) {
        uiState = uiState.copy(selectedProblemField = problemField)
    }

    fun selectRecommendation(recommendation: MeditationRecommendation) {
        uiState = uiState.copy(selectedRecommendation = recommendation)
    }

    fun setRecommendations(recommendations: List<MeditationRecommendation>) {
        uiState = uiState.copy(recommendations = recommendations)
    }

    fun updatePreSessionQuestionnaire(questionnaire: PreSessionQuestionnaire) {
        uiState = uiState.copy(preSessionQuestionnaire = questionnaire)
    }

    fun updatePostSessionQuestionnaire(questionnaire: PostSessionQuestionnaire) {
        uiState = uiState.copy(postSessionQuestionnaire = questionnaire)
    }

    fun updateBloodPressureBefore(value: BloodPressureMeasurement) {
        uiState = uiState.copy(bloodPressureBefore = value)
    }

    fun updateBloodPressureAfter(value: BloodPressureMeasurement) {
        uiState = uiState.copy(bloodPressureAfter = value)
    }

    fun updateEffectiveness(value: MeditationEffectiveness) {
        uiState = uiState.copy(effectiveness = value)
    }

    fun updateUserPreferences(value: UserPreferences) {
        uiState = uiState.copy(userPreferences = value)
    }

    fun addCompletedSession(record: CompletedSessionRecord) {
        uiState = uiState.copy(
            completedSessions = uiState.completedSessions + record
        )
    }

    fun startSession() {
        val recommendation = uiState.selectedRecommendation
        val problemField = uiState.selectedProblemField

        val session = MeditationSession(
            sessionId = "session_${System.currentTimeMillis()}",
            problemFieldId = problemField?.id,
            recommendationId = recommendation?.id,
            recommendationTitle = recommendation?.title,
            startedAtMillis = System.currentTimeMillis(),
            isRunning = true
        )

        uiState = uiState.copy(currentSession = session)
    }

    fun stopSession() {
        uiState = uiState.copy(currentSession = null)
    }

    fun updateLatestPayload(payload: String) {
        uiState = uiState.copy(latestPayload = payload)
    }

    fun updateLiveBioText(text: String) {
        uiState = uiState.copy(liveBioText = text)
    }

    fun updateSendStatus(text: String) {
        uiState = uiState.copy(sendStatus = text)
    }
}