package com.example.meditationbio.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.ProblemField
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

    fun setRecommendations(recommendations: List<MeditationRecommendation>) {
        uiState = uiState.copy(recommendations = recommendations)
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