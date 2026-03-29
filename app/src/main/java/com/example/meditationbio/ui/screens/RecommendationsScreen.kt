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
fun RecommendationsScreen(
    selectedProblemField: ProblemField?,
    recommendations: List<MeditationRecommendation>,
    onBack: () -> Unit,
    onRecommendationSelected: (MeditationRecommendation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = "Empfehlungen",
            subtitle = "Passende Meditationen für ${selectedProblemField?.title ?: "dein aktuelles Problemfeld"}."
        )

        SecondaryButton(
            text = "Zurück",
            onClick = onBack
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            InfoBadge(text = "Lernprofil aktiv")
        }

        recommendations.forEach { recommendation ->
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
                        onClick = { onRecommendationSelected(recommendation) },
                        modifier = Modifier.padding(top = 18.dp)
                    )
                }
            }
        }
    }
}