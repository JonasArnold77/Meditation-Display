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
fun EditorScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Editor")

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        Text(
            text = "Hier kommt in Phase 2 dein bestehender freier Parameter-Editor hinein.",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}