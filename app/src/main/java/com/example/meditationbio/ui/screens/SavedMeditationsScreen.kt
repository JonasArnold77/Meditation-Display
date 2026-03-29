package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.ProblemField
import com.example.meditationbio.ui.components.InfoBadge
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SecondaryButton
import com.example.meditationbio.ui.components.SectionCard

@androidx.compose.runtime.Composable
fun SavedMeditationsScreen(
    selectedProblemField: ProblemField?,
    savedRecommendations: List<MeditationRecommendation>,
    onBack: () -> Unit,
    onOpenRecommendation: (MeditationRecommendation) -> Unit,
    onRemoveRecommendation: (MeditationRecommendation) -> Unit
) {
    val filteredRecommendations = savedRecommendations.filter {
        it.problemFieldId == selectedProblemField?.id
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = "Gespeicherte Meditationen",
            subtitle = "Gespeicherte Vorschläge für ${selectedProblemField?.title ?: "dieses Problemfeld"}."
        )

        SecondaryButton(
            text = "Zurück",
            onClick = onBack
        )

        if (filteredRecommendations.isEmpty()) {
            SectionCard(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Für dieses Problemfeld wurden noch keine Meditationen gespeichert.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            filteredRecommendations.forEach { recommendation ->
                SectionCard(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = recommendation.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = recommendation.subtitle,
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(modifier = Modifier.padding(top = 14.dp)) {
                            InfoBadge(text = "${recommendation.durationMinutes} Min")
                        }

                        Row(modifier = Modifier.padding(top = 10.dp)) {
                            InfoBadge(text = recommendation.style)
                        }

                        Row(modifier = Modifier.padding(top = 10.dp)) {
                            InfoBadge(text = "Wirkung: ${recommendation.effectivenessLabel}")
                        }

                        PrimaryButton(
                            text = "Details / Start",
                            onClick = { onOpenRecommendation(recommendation) },
                            modifier = Modifier.padding(top = 18.dp)
                        )

                        SecondaryButton(
                            text = "Aus gespeichert entfernen",
                            onClick = { onRemoveRecommendation(recommendation) },
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }
    }
}