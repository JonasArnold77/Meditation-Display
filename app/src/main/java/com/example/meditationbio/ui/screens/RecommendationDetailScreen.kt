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
fun RecommendationDetailScreen(
    selectedProblemField: ProblemField?,
    recommendation: MeditationRecommendation?,
    onBack: () -> Unit,
    onStartMeditation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Meditationsvorschlag")

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        Text(
            text = "Problemfeld: ${selectedProblemField?.title ?: "-"}",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Titel: ${recommendation?.title ?: "-"}",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Beschreibung: ${recommendation?.subtitle ?: "-"}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Dauer: ${recommendation?.durationMinutes ?: 0} Minuten",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Stil: ${recommendation?.style ?: "-"}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Ton: ${recommendation?.tone ?: "-"}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Wirksamkeit zuletzt: ${recommendation?.effectivenessLabel ?: "-"}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = onStartMeditation,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Meditation vorbereiten")
        }
    }
}