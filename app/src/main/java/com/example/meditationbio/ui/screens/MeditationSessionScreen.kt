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
import com.example.meditationbio.model.BloodPressureMeasurement
import com.example.meditationbio.model.MeditationSession
import com.example.meditationbio.model.ProblemField
import com.example.meditationbio.ui.components.InfoBadge
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SectionCard

@androidx.compose.runtime.Composable
fun MeditationSessionScreen(
    session: MeditationSession?,
    selectedProblemField: ProblemField?,
    bloodPressureBefore: BloodPressureMeasurement,
    liveBioText: String,
    latestPayload: String,
    onStopSession: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = "Meditation läuft",
            subtitle = "Biofeedback und Status deiner aktuellen Session."
        )

        SectionCard {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = session?.recommendationTitle ?: "-",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Problemfeld: ${selectedProblemField?.title ?: "-"}",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(modifier = Modifier.padding(top = 12.dp)) {
                    InfoBadge(text = "Session aktiv")
                }

                Text(
                    text = "Session-ID: ${session?.sessionId ?: "-"}",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionCard(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Blutdruck vorher",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${bloodPressureBefore.systolic}/${bloodPressureBefore.diastolic}",
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        SectionCard(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Live Bio-Daten",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = liveBioText,
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionCard(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Letzter Roh-Payload",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = latestPayload,
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        PrimaryButton(
            text = "Meditation beenden",
            onClick = onStopSession,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}