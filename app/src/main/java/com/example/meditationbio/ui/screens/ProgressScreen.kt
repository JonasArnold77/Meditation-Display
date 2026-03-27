package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.CompletedSessionRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@androidx.compose.runtime.Composable
fun ProgressScreen(
    completedSessions: List<CompletedSessionRecord>,
    onBack: () -> Unit
) {
    val totalSessions = completedSessions.size
    val averageScore = if (completedSessions.isNotEmpty()) {
        completedSessions.map { it.effectivenessScore }.average().toInt()
    } else {
        0
    }

    val bestProblemField = completedSessions
        .groupBy { it.problemFieldId }
        .mapValues { entry -> entry.value.map { it.effectivenessScore }.average() }
        .maxByOrNull { it.value }
        ?.key ?: "-"

    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Fortschritt")

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        Text(
            text = "Sessions insgesamt: $totalSessions",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Durchschnittsscore: $averageScore",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Bestes Problemfeld bisher: $bestProblemField",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Verlauf",
            modifier = Modifier.padding(top = 24.dp)
        )

        if (completedSessions.isEmpty()) {
            Text(
                text = "Noch keine abgeschlossenen Sessions vorhanden.",
                modifier = Modifier.padding(top = 12.dp)
            )
        } else {
            completedSessions
                .sortedByDescending { it.timestampMillis }
                .forEach { session ->
                    Text(
                        text = session.recommendationTitle,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text(
                        text = "Problemfeld: ${session.problemFieldId}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Score: ${session.effectivenessScore}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Fazit: ${session.summary}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Zeitpunkt: ${formatter.format(Date(session.timestampMillis))}",
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
        }
    }
}