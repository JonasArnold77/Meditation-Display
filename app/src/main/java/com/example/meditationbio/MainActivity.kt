package com.example.meditationbio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.meditationbio.data.ProblemFieldRepository
import com.example.meditationbio.data.RecommendationRepository
import com.example.meditationbio.logic.MeditationEffectivenessEvaluator
import com.example.meditationbio.model.CompletedSessionRecord
import com.example.meditationbio.state.AppStateStore
import com.example.meditationbio.ui.AppScreen
import com.example.meditationbio.ui.screens.BloodPressureInputScreen
import com.example.meditationbio.ui.screens.EditorScreen
import com.example.meditationbio.ui.screens.HomeScreen
import com.example.meditationbio.ui.screens.MeditationSessionScreen
import com.example.meditationbio.ui.screens.PostSessionQuestionnaireScreen
import com.example.meditationbio.ui.screens.PreSessionQuestionnaireScreen
import com.example.meditationbio.ui.screens.ProblemFieldsScreen
import com.example.meditationbio.ui.screens.ProgressScreen
import com.example.meditationbio.ui.screens.RecommendationDetailScreen
import com.example.meditationbio.ui.screens.RecommendationsScreen
import com.example.meditationbio.ui.screens.SessionResultScreen

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
                            sendStatus = uiState.sendStatus,
                            onOpenProblemFields = {
                                AppStateStore.navigateTo(AppScreen.PROBLEM_FIELDS)
                            },
                            onOpenEditor = {
                                AppStateStore.navigateTo(AppScreen.EDITOR)
                            },
                            onOpenProgress = {
                                AppStateStore.navigateTo(AppScreen.PROGRESS)
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
                                    RecommendationRepository.getForProblemField(
                                        problemFieldId = field.id,
                                        completedSessions = uiState.completedSessions
                                    )
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
                            },
                            onRecommendationSelected = { recommendation ->
                                AppStateStore.selectRecommendation(recommendation)
                                AppStateStore.navigateTo(AppScreen.RECOMMENDATION_DETAIL)
                            }
                        )
                    }

                    AppScreen.RECOMMENDATION_DETAIL -> {
                        RecommendationDetailScreen(
                            selectedProblemField = uiState.selectedProblemField,
                            recommendation = uiState.selectedRecommendation,
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.RECOMMENDATIONS)
                            },
                            onStartMeditation = {
                                AppStateStore.navigateTo(AppScreen.PRE_SESSION_QUESTIONNAIRE)
                            }
                        )
                    }

                    AppScreen.PRE_SESSION_QUESTIONNAIRE -> {
                        PreSessionQuestionnaireScreen(
                            initialValue = uiState.preSessionQuestionnaire,
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.RECOMMENDATION_DETAIL)
                            },
                            onContinue = { questionnaire ->
                                AppStateStore.updatePreSessionQuestionnaire(questionnaire)
                                AppStateStore.navigateTo(AppScreen.PRE_BLOOD_PRESSURE)
                            }
                        )
                    }

                    AppScreen.PRE_BLOOD_PRESSURE -> {
                        BloodPressureInputScreen(
                            title = "Blutdruck vor der Meditation",
                            initialValue = uiState.bloodPressureBefore,
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.PRE_SESSION_QUESTIONNAIRE)
                            },
                            onContinue = { value ->
                                AppStateStore.updateBloodPressureBefore(value)
                                AppStateStore.startSession()
                                AppStateStore.updateSendStatus(
                                    "Vorher-Check und Blutdruck vorher gespeichert. Session gestartet."
                                )
                                AppStateStore.navigateTo(AppScreen.MEDITATION_SESSION)
                            }
                        )
                    }

                    AppScreen.MEDITATION_SESSION -> {
                        MeditationSessionScreen(
                            session = uiState.currentSession,
                            selectedProblemField = uiState.selectedProblemField,
                            bloodPressureBefore = uiState.bloodPressureBefore,
                            liveBioText = uiState.liveBioText,
                            latestPayload = uiState.latestPayload,
                            onStopSession = {
                                // WICHTIG: Session hier NICHT löschen
                                AppStateStore.navigateTo(AppScreen.POST_BLOOD_PRESSURE)
                            }
                        )
                    }

                    AppScreen.POST_BLOOD_PRESSURE -> {
                        BloodPressureInputScreen(
                            title = "Blutdruck nach der Meditation",
                            initialValue = uiState.bloodPressureAfter,
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.MEDITATION_SESSION)
                            },
                            onContinue = { value ->
                                AppStateStore.updateBloodPressureAfter(value)
                                AppStateStore.navigateTo(AppScreen.POST_SESSION_QUESTIONNAIRE)
                            }
                        )
                    }

                    AppScreen.POST_SESSION_QUESTIONNAIRE -> {
                        PostSessionQuestionnaireScreen(
                            initialValue = uiState.postSessionQuestionnaire,
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.POST_BLOOD_PRESSURE)
                            },
                            onContinue = { questionnaire ->
                                MobileWearService.sendCompletedSessionToN8n(
                                    session = uiState.currentSession,
                                    problemField = uiState.selectedProblemField,
                                    recommendation = uiState.selectedRecommendation,
                                    preQuestionnaire = uiState.preSessionQuestionnaire,
                                    postQuestionnaire = questionnaire,
                                    bloodPressureBefore = uiState.bloodPressureBefore,
                                    bloodPressureAfter = uiState.bloodPressureAfter,
                                    latestPayload = uiState.latestPayload,
                                    liveBioText = uiState.liveBioText
                                )

                                AppStateStore.updatePostSessionQuestionnaire(questionnaire)

                                val effectiveness = MeditationEffectivenessEvaluator.evaluate(
                                    pre = uiState.preSessionQuestionnaire,
                                    post = questionnaire,
                                    bpBefore = uiState.bloodPressureBefore,
                                    bpAfter = uiState.bloodPressureAfter
                                )

                                AppStateStore.updateEffectiveness(effectiveness)

                                val recommendation = uiState.selectedRecommendation
                                val problemField = uiState.selectedProblemField
                                val session = uiState.currentSession

                                if (recommendation != null && problemField != null && session != null) {
                                    AppStateStore.addCompletedSession(
                                        CompletedSessionRecord(
                                            sessionId = session.sessionId,
                                            problemFieldId = problemField.id,
                                            recommendationId = recommendation.id,
                                            recommendationTitle = recommendation.title,
                                            effectivenessScore = effectiveness.score,
                                            summary = effectiveness.summary,
                                            timestampMillis = System.currentTimeMillis()
                                        )
                                    )
                                }

                                AppStateStore.updateSendStatus(
                                    "Session abgeschlossen. Daten wurden an n8n gesendet."
                                )
                                AppStateStore.navigateTo(AppScreen.SESSION_RESULT)
                            }
                        )
                    }

                    AppScreen.SESSION_RESULT -> {
                        SessionResultScreen(
                            effectiveness = uiState.effectiveness,
                            onOpenProblemFields = {
                                // Erst jetzt Session aufräumen
                                AppStateStore.stopSession()
                                AppStateStore.navigateTo(AppScreen.PROBLEM_FIELDS)
                            },
                            onContinue = {
                                // Erst jetzt Session aufräumen
                                AppStateStore.stopSession()
                                AppStateStore.navigateTo(AppScreen.HOME)
                            }
                        )
                    }

                    AppScreen.PROGRESS -> {
                        ProgressScreen(
                            completedSessions = uiState.completedSessions,
                            onBack = {
                                AppStateStore.navigateTo(AppScreen.HOME)
                            }
                        )
                    }

                    AppScreen.EDITOR -> {
                        EditorScreen(
                            sendStatus = uiState.sendStatus,
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