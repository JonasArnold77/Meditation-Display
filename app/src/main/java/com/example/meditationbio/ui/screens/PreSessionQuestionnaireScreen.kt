package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.PreSessionQuestionnaire

@androidx.compose.runtime.Composable
fun PreSessionQuestionnaireScreen(
    initialValue: PreSessionQuestionnaire,
    onBack: () -> Unit,
    onContinue: (PreSessionQuestionnaire) -> Unit
) {
    var emotionalLoad by remember { mutableFloatStateOf(initialValue.emotionalLoad.toFloat()) }
    var innerRestlessness by remember { mutableFloatStateOf(initialValue.innerRestlessness.toFloat()) }
    var overthinking by remember { mutableFloatStateOf(initialValue.overthinking.toFloat()) }
    var opennessForMeditation by remember { mutableFloatStateOf(initialValue.opennessForMeditation.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Vorher-Check")

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        Text(
            text = "Wie belastet fühlst du dich gerade? (${emotionalLoad.toInt()})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = emotionalLoad,
            onValueChange = { emotionalLoad = it },
            valueRange = 0f..10f,
            steps = 9
        )

        Text(
            text = "Wie unruhig fühlst du dich? (${innerRestlessness.toInt()})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = innerRestlessness,
            onValueChange = { innerRestlessness = it },
            valueRange = 0f..10f,
            steps = 9
        )

        Text(
            text = "Wie stark ist dein Gedankenkarussell? (${overthinking.toInt()})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = overthinking,
            onValueChange = { overthinking = it },
            valueRange = 0f..10f,
            steps = 9
        )

        Text(
            text = "Wie offen bist du gerade für Meditation? (${opennessForMeditation.toInt()})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = opennessForMeditation,
            onValueChange = { opennessForMeditation = it },
            valueRange = 0f..10f,
            steps = 9
        )

        Button(
            onClick = {
                onContinue(
                    PreSessionQuestionnaire(
                        emotionalLoad = emotionalLoad.toInt(),
                        innerRestlessness = innerRestlessness.toInt(),
                        overthinking = overthinking.toInt(),
                        opennessForMeditation = opennessForMeditation.toInt()
                    )
                )
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Weiter")
        }
    }
}