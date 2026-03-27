package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@androidx.compose.runtime.Composable
fun HomeScreen(
    liveBioText: String,
    sendStatus: String,
    onOpenProblemFields: () -> Unit,
    onOpenEditor: () -> Unit,
    onOpenProgress: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Meditation Bio")

        Text(
            text = "Wähle einen Einstieg:",
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(
            onClick = onOpenProblemFields,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Problemfelder")
        }

        Button(
            onClick = onOpenEditor,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Editor")
        }

        Button(
            onClick = onOpenProgress,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Fortschritt")
        }

        Text(
            text = "Status",
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = sendStatus,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Live Bio-Daten",
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = liveBioText,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}