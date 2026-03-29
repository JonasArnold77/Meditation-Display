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
        val index = uiState.recommendations.indexOfFirst { it.id == recommendation.id }
        uiState = uiState.copy(
            selectedRecommendation = recommendation,
            currentRecommendationIndex = if (index >= 0) index else uiState.currentRecommendationIndex
        )
    }

    fun setRecommendations(recommendations: List<MeditationRecommendation>) {
        uiState = uiState.copy(
            recommendations = recommendations,
            currentRecommendationIndex = 0,
            selectedRecommendation = recommendations.firstOrNull()
        )
    }

    fun moveToNextRecommendation() {
        val recommendations = uiState.recommendations
        if (recommendations.isEmpty()) return

        val nextIndex = (uiState.currentRecommendationIndex + 1) % recommendations.size
        uiState = uiState.copy(
            currentRecommendationIndex = nextIndex,
            selectedRecommendation = recommendations[nextIndex],
            sendStatus = "Nächster Meditationsvorschlag geladen."
        )
    }

    fun saveCurrentRecommendation() {
        val current = uiState.recommendations.getOrNull(uiState.currentRecommendationIndex) ?: return
        val alreadySaved = uiState.savedRecommendations.any { it.id == current.id }

        uiState = if (alreadySaved) {
            uiState.copy(sendStatus = "Diese Meditation ist bereits gespeichert.")
        } else {
            uiState.copy(
                savedRecommendations = uiState.savedRecommendations + current,
                sendStatus = "Meditation gespeichert: ${current.title}"
            )
        }
    }

    fun removeSavedRecommendation(recommendationId: String) {
        uiState = uiState.copy(
            savedRecommendations = uiState.savedRecommendations.filterNot { it.id == recommendationId },
            sendStatus = "Meditation aus gespeichertem Menü entfernt."
        )
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

    fun setMeditationGenerating(value: Boolean) {
        uiState = uiState.copy(isGeneratingMeditation = value)
    }

    fun setGeneratedMeditationText(text: String?) {
        uiState = uiState.copy(generatedMeditationText = text)
    }

    fun showGeneratedMeditationDialog(show: Boolean) {
        uiState = uiState.copy(showGeneratedMeditationDialog = show)
    }
}