package com.example.meditationbio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.example.meditationbio.ui.screens.ProfileScreen
import com.example.meditationbio.ui.screens.ProblemFieldsScreen
import com.example.meditationbio.ui.screens.ProgressScreen
import com.example.meditationbio.ui.screens.RecommendationDetailScreen
import com.example.meditationbio.ui.screens.RecommendationsScreen
import com.example.meditationbio.ui.screens.SavedMeditationsScreen
import com.example.meditationbio.ui.screens.SessionResultScreen
import com.example.meditationbio.ui.theme.MeditationBioTheme

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

        fun setMeditationGenerating(value: Boolean) {
            AppStateStore.setMeditationGenerating(value)
        }

        fun setGeneratedMeditationText(text: String?) {
            AppStateStore.setGeneratedMeditationText(text)
        }

        fun showGeneratedMeditationDialog(show: Boolean) {
            AppStateStore.showGeneratedMeditationDialog(show)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MeditationBioTheme {
                Surface {
                    val uiState = AppStateStore.uiState

                    if (uiState.isGeneratingMeditation && uiState.generatedMeditationText.isNullOrBlank()) {
                        AlertDialog(
                            onDismissRequest = { },
                            confirmButton = {},
                            title = {
                                Text("Meditation wird geladen")
                            },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    CircularProgressIndicator()
                                    Text(
                                        text = "Bitte warten, der Meditationstext wird gerade von n8n geladen.",
                                        modifier = Modifier.padding(top = 16.dp)
                                    )
                                }
                            }
                        )
                    }

                    if (
                        uiState.showGeneratedMeditationDialog &&
                        !uiState.generatedMeditationText.isNullOrBlank() &&
                        !uiState.isGeneratingMeditation
                    ) {
                        AlertDialog(
                            onDismissRequest = {
                                AppStateStore.showGeneratedMeditationDialog(false)
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        AppStateStore.showGeneratedMeditationDialog(false)
                                    }
                                ) {
                                    Text("Schließen")
                                }
                            },
                            title = {
                                Text("Generierte Meditation")
                            },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(uiState.generatedMeditationText ?: "")
                                }
                            }
                        )
                    }

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
                                },
                                onOpenProfile = {
                                    AppStateStore.navigateTo(AppScreen.PROFILE)
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
                                currentRecommendationIndex = uiState.currentRecommendationIndex,
                                onBack = {
                                    AppStateStore.navigateTo(AppScreen.PROBLEM_FIELDS)
                                },
                                onSwipeLeft = {
                                    AppStateStore.moveToNextRecommendation()
                                },
                                onSwipeRight = {
                                    AppStateStore.saveCurrentRecommendation()
                                },
                                onOpenSavedMeditations = {
                                    AppStateStore.navigateTo(AppScreen.SAVED_MEDITATIONS)
                                },
                                onRecommendationSelected = { recommendation ->
                                    AppStateStore.selectRecommendation(recommendation)
                                    AppStateStore.navigateTo(AppScreen.RECOMMENDATION_DETAIL)
                                }
                            )
                        }

                        AppScreen.SAVED_MEDITATIONS -> {
                            SavedMeditationsScreen(
                                selectedProblemField = uiState.selectedProblemField,
                                savedRecommendations = uiState.savedRecommendations,
                                onBack = {
                                    AppStateStore.navigateTo(AppScreen.RECOMMENDATIONS)
                                },
                                onOpenRecommendation = { recommendation ->
                                    AppStateStore.selectRecommendation(recommendation)
                                    AppStateStore.navigateTo(AppScreen.RECOMMENDATION_DETAIL)
                                },
                                onRemoveRecommendation = { recommendation ->
                                    AppStateStore.removeSavedRecommendation(recommendation.id)
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

                                    val recommendation = uiState.selectedRecommendation
                                    val problemField = uiState.selectedProblemField

                                    val goal = when {
                                        recommendation?.title?.isNotBlank() == true -> recommendation.title
                                        problemField?.title?.isNotBlank() == true -> problemField.title
                                        else -> "innere Ruhe"
                                    }

                                    val durationMinutes = recommendation?.durationMinutes ?: 10

                                    val style = when {
                                        recommendation?.style?.isNotBlank() == true -> recommendation.style
                                        else -> "Atemmeditation"
                                    }

                                    val tone = when {
                                        recommendation?.tone?.isNotBlank() == true -> recommendation.tone
                                        problemField?.title?.contains("stress", ignoreCase = true) == true -> "sanft und beruhigend"
                                        problemField?.title?.contains("angst", ignoreCase = true) == true -> "ruhig und stabilisierend"
                                        else -> "sanft und warm"
                                    }

                                    val context = when {
                                        problemField?.title?.contains("schlaf", ignoreCase = true) == true -> "abends"
                                        else -> "tagsüber"
                                    }

                                    val focus = when {
                                        recommendation?.subtitle?.isNotBlank() == true -> recommendation.subtitle
                                        problemField?.subtitle?.isNotBlank() == true -> problemField.subtitle
                                        else -> "Atmung"
                                    }

                                    val specialNotes = buildString {
                                        append("Problemfeld: ")
                                        append(problemField?.title ?: "Allgemein")
                                        append(". ")
                                        if (!problemField?.description.isNullOrBlank()) {
                                            append("Beschreibung: ${problemField?.description}. ")
                                        }
                                        if (!recommendation?.subtitle.isNullOrBlank()) {
                                            append("Empfehlung: ${recommendation?.subtitle}. ")
                                        }
                                        append("Bitte direkt vorlesbar formulieren und passend für die laufende Session.")
                                    }

                                    MobileWearService.sendMeditationConfigToN8n(
                                        language = "de",
                                        goal = goal,
                                        durationMinutes = durationMinutes,
                                        experienceLevel = "Anfänger",
                                        style = style,
                                        tone = tone,
                                        targetAudience = "Erwachsene",
                                        spiritual = false,
                                        context = context,
                                        focus = focus,
                                        musicRecommendation = true,
                                        specialNotes = specialNotes
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
                                    AppStateStore.stopSession()
                                    AppStateStore.navigateTo(AppScreen.PROBLEM_FIELDS)
                                },
                                onContinue = {
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

                        AppScreen.PROFILE -> {
                            ProfileScreen(
                                initialPreferences = uiState.userPreferences,
                                onBack = {
                                    AppStateStore.navigateTo(AppScreen.HOME)
                                },
                                onSave = { preferences ->
                                    AppStateStore.updateUserPreferences(preferences)
                                    AppStateStore.updateSendStatus("Präferenzen gespeichert.")
                                    AppStateStore.navigateTo(AppScreen.HOME)
                                }
                            )
                        }

                        AppScreen.EDITOR -> {
                            EditorScreen(
                                sendStatus = uiState.sendStatus,
                                isGeneratingMeditation = uiState.isGeneratingMeditation,
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
}