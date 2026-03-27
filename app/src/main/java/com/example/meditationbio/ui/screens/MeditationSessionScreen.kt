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
import com.example.meditationbio.model.BloodPressureMeasurement
import com.example.meditationbio.model.MeditationSession
import com.example.meditationbio.model.ProblemField

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
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Meditation läuft")

        Text(
            text = "Problemfeld: ${selectedProblemField?.title ?: "-"}",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Meditation: ${session?.recommendationTitle ?: "-"}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Session-ID: ${session?.sessionId ?: "-"}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Blutdruck vorher: ${bloodPressureBefore.systolic}/${bloodPressureBefore.diastolic}",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Live Bio-Daten",
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = liveBioText,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Letzter Roh-Payload",
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = latestPayload,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = onStopSession,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Meditation beenden")
        }
    }
}