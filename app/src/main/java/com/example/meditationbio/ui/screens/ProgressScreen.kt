package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.CompletedSessionRecord
import com.example.meditationbio.ui.components.MetricCard
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SecondaryButton
import com.example.meditationbio.ui.components.SectionCard
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
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = "Fortschritt",
            subtitle = "Dein Verlauf, deine Scores und erste Muster in deiner Praxis."
        )

        SecondaryButton(
            text = "Zurück",
            onClick = onBack
        )

        MetricCard(
            title = "Sessions insgesamt",
            value = "$totalSessions",
            modifier = Modifier.padding(top = 16.dp)
        )

        MetricCard(
            title = "Durchschnittsscore",
            value = "$averageScore",
            modifier = Modifier.padding(top = 16.dp)
        )

        MetricCard(
            title = "Bestes Problemfeld",
            value = bestProblemField,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Verlauf",
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (completedSessions.isEmpty()) {
            SectionCard(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Noch keine abgeschlossenen Sessions vorhanden.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            completedSessions
                .sortedByDescending { it.timestampMillis }
                .forEach { session ->
                    SectionCard(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = session.recommendationTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Problemfeld: ${session.problemFieldId}",
                                modifier = Modifier.padding(top = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Score: ${session.effectivenessScore}",
                                modifier = Modifier.padding(top = 6.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = session.summary,
                                modifier = Modifier.padding(top = 6.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = formatter.format(Date(session.timestampMillis)),
                                modifier = Modifier.padding(top = 10.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
        }
    }
}