package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.PostSessionQuestionnaire

@androidx.compose.runtime.Composable
fun PostSessionQuestionnaireScreen(
    initialValue: PostSessionQuestionnaire,
    onBack: () -> Unit,
    onContinue: (PostSessionQuestionnaire) -> Unit
) {
    var calmnessNow by remember { mutableFloatStateOf(initialValue.calmnessNow.toFloat()) }
    var relief by remember { mutableFloatStateOf(initialValue.relief.toFloat()) }
    var focusNow by remember { mutableFloatStateOf(initialValue.focusNow.toFloat()) }
    var helpfulness by remember { mutableFloatStateOf(initialValue.helpfulness.toFloat()) }
    var wantSimilarMeditations by remember { mutableStateOf(initialValue.wantSimilarMeditations) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Nachher-Check")

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        Text(
            text = "Wie ruhig fühlst du dich jetzt? (${calmnessNow.toInt()})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = calmnessNow,
            onValueChange = { calmnessNow = it },
            valueRange = 0f..10f,
            steps = 9
        )

        Text(
            text = "Wie stark fühlst du Entlastung? (${relief.toInt()})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = relief,
            onValueChange = { relief = it },
            valueRange = 0f..10f,
            steps = 9
        )

        Text(
            text = "Wie fokussiert fühlst du dich jetzt? (${focusNow.toInt()})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = focusNow,
            onValueChange = { focusNow = it },
            valueRange = 0f..10f,
            steps = 9
        )

        Text(
            text = "Wie hilfreich war die Meditation? (${helpfulness.toInt()})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = helpfulness,
            onValueChange = { helpfulness = it },
            valueRange = 0f..10f,
            steps = 9
        )

        Text(
            text = "Ähnliche Meditationen künftig bevorzugen?",
            modifier = Modifier.padding(top = 16.dp)
        )
        Checkbox(
            checked = wantSimilarMeditations,
            onCheckedChange = { wantSimilarMeditations = it }
        )

        Button(
            onClick = {
                onContinue(
                    PostSessionQuestionnaire(
                        calmnessNow = calmnessNow.toInt(),
                        relief = relief.toInt(),
                        focusNow = focusNow.toInt(),
                        helpfulness = helpfulness.toInt(),
                        wantSimilarMeditations = wantSimilarMeditations
                    )
                )
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Session abschließen")
        }
    }
}