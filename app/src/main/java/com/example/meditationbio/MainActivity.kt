package com.example.meditationbio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.meditationbio.data.ProblemFieldRepository
import com.example.meditationbio.data.RecommendationRepository
import com.example.meditationbio.state.AppStateStore
import com.example.meditationbio.ui.AppScreen
import com.example.meditationbio.ui.screens.EditorScreen
import com.example.meditationbio.ui.screens.HomeScreen
import com.example.meditationbio.ui.screens.ProblemFieldsScreen
import com.example.meditationbio.ui.screens.RecommendationsScreen

class MainActivity : ComponentActivity() {

    companion object {
        fun updateLatestPayload(payload: String) {
            AppStateStore.updateLatestPayload(payload)
        }

        fun updateLiveBioText(text: String) {
            AppStateStore.updateLiveBioText(text)
        }

        fun updateSendStatus(text: String) {
            AppStateStore.updateSendStatus(text)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                val uiState = AppStateStore.uiState

                when (uiState.currentScreen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            liveBioText = uiState.liveBioText,
                            onOpenProblemFields = {
                                AppStateStore.navigateTo(AppScreen.PROBLEM_FIELDS)
                            },
                            onOpenEditor = {
                                AppStateStore.navigateTo(AppScreen.EDITOR)
                            }
                        )
                    }

                    AppScreen.PROBLEM_FIELDS -> {
                        ProblemFieldsScreen(
                            problemFields = ProblemFieldRepository.getAll(),
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.HOME)
                            },
                            onProblemFieldSelected = { field ->
                                AppStateStore.selectProblemField(field)
                                AppStateStore.setRecommendations(
                                    RecommendationRepository.getForProblemField(field.id)
                                )
                                AppStateStore.navigateTo(AppScreen.RECOMMENDATIONS)
                            }
                        )
                    }

                    AppScreen.RECOMMENDATIONS -> {
                        RecommendationsScreen(
                            selectedProblemField = uiState.selectedProblemField,
                            recommendations = uiState.recommendations,
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.PROBLEM_FIELDS)
                            }
                        )
                    }

                    AppScreen.EDITOR -> {
                        EditorScreen(
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.HOME)
                            }
                        )
                    }
                }
            }
        }
    }
}