package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.MeditationEffectiveness

@androidx.compose.runtime.Composable
fun SessionResultScreen(
    effectiveness: MeditationEffectiveness?,
    onContinue: () -> Unit,
    onOpenProblemFields: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Session-Ergebnis")

        Text(
            text = "Gesamtscore: ${effectiveness?.score ?: 0}/100",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Subjektive Veränderung: ${effectiveness?.subjectiveImprovement ?: 0}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Blutdruck Veränderung systolisch: ${effectiveness?.bloodPressureDeltaSystolic ?: 0}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Blutdruck Veränderung diastolisch: ${effectiveness?.bloodPressureDeltaDiastolic ?: 0}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = effectiveness?.summary ?: "-",
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(
            onClick = onOpenProblemFields,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Weitere Empfehlung ansehen")
        }

        Button(
            onClick = onContinue,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zur Startseite")
        }
    }
}