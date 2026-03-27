package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.MeditationRecommendation
import com.example.meditationbio.model.ProblemField

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
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Empfehlungen")

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        Text(
            text = "Für: ${selectedProblemField?.title ?: "-"}",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Lernprofil aktiv",
            modifier = Modifier.padding(top = 8.dp)
        )

        recommendations.forEach { recommendation ->
            Text(
                text = recommendation.title,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = recommendation.subtitle,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Dauer: ${recommendation.durationMinutes} Min | Stil: ${recommendation.style} | Wirkung zuletzt: ${recommendation.effectivenessLabel}",
                modifier = Modifier.padding(top = 4.dp)
            )

            Button(
                onClick = { onRecommendationSelected(recommendation) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Details / Start")
            }
        }
    }
}