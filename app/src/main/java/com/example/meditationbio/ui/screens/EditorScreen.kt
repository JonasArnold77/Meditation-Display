package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.MobileWearService
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SecondaryButton
import com.example.meditationbio.ui.components.SectionCard

@androidx.compose.runtime.Composable
fun EditorScreen(
    sendStatus: String,
    isGeneratingMeditation: Boolean,
    onBack: () -> Unit
) {
    var language by remember { mutableStateOf("de") }
    var goal by remember { mutableStateOf("innere Ruhe") }
    var durationMinutes by remember { mutableStateOf("10") }
    var experienceLevel by remember { mutableStateOf("Anfänger") }
    var style by remember { mutableStateOf("Atemmeditation") }
    var tone by remember { mutableStateOf("sanft und warm") }
    var targetAudience by remember { mutableStateOf("Erwachsene") }
    var spiritual by remember { mutableStateOf(false) }
    var context by remember { mutableStateOf("abends") }
    var focus by remember { mutableStateOf("Atmung") }
    var musicRecommendation by remember { mutableStateOf(true) }
    var specialNotes by remember { mutableStateOf("keine esoterische Sprache") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = "Editor",
            subtitle = "Erstelle freie Meditationen anhand deiner eigenen Parameter."
        )

        SecondaryButton(
            text = "Zurück",
            onClick = onBack
        )

        SectionCard(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = language,
                    onValueChange = { language = it },
                    label = { Text("language") }
                )

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("goal") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = durationMinutes,
                    onValueChange = { durationMinutes = it },
                    label = { Text("duration_minutes") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = experienceLevel,
                    onValueChange = { experienceLevel = it },
                    label = { Text("experience_level") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = style,
                    onValueChange = { style = it },
                    label = { Text("style") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = tone,
                    onValueChange = { tone = it },
                    label = { Text("tone") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = targetAudience,
                    onValueChange = { targetAudience = it },
                    label = { Text("target_audience") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                Text(
                    text = "spiritual",
                    modifier = Modifier.padding(top = 16.dp)
                )
                Checkbox(
                    checked = spiritual,
                    onCheckedChange = { spiritual = it }
                )

                OutlinedTextField(
                    value = context,
                    onValueChange = { context = it },
                    label = { Text("context") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = focus,
                    onValueChange = { focus = it },
                    label = { Text("focus") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                Text(
                    text = "music_recommendation",
                    modifier = Modifier.padding(top = 16.dp)
                )
                Checkbox(
                    checked = musicRecommendation,
                    onCheckedChange = { musicRecommendation = it }
                )

                OutlinedTextField(
                    value = specialNotes,
                    onValueChange = { specialNotes = it },
                    label = { Text("special_notes") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                PrimaryButton(
                    text = if (isGeneratingMeditation) {
                        "Meditation wird erstellt..."
                    } else {
                        "Meditation an n8n senden"
                    },
                    onClick = {
                        if (!isGeneratingMeditation) {
                            MobileWearService.sendMeditationConfigToN8n(
                                language = language,
                                goal = goal,
                                durationMinutes = durationMinutes.toIntOrNull() ?: 10,
                                experienceLevel = experienceLevel,
                                style = style,
                                tone = tone,
                                targetAudience = targetAudience,
                                spiritual = spiritual,
                                context = context,
                                focus = focus,
                                musicRecommendation = musicRecommendation,
                                specialNotes = specialNotes
                            )
                        }
                    },
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }

        SectionCard(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Status")
                Text(
                    text = sendStatus,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}