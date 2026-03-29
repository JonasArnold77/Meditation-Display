package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.UserPreferences
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SecondaryButton
import com.example.meditationbio.ui.components.SectionCard

@androidx.compose.runtime.Composable
fun ProfileScreen(
    initialPreferences: UserPreferences,
    onBack: () -> Unit,
    onSave: (UserPreferences) -> Unit
) {
    var preferredDuration by remember {
        mutableStateOf(initialPreferences.preferredDurationMinutes.toString())
    }
    var preferredTone by remember {
        mutableStateOf(initialPreferences.preferredTone)
    }
    var preferredStyle by remember {
        mutableStateOf(initialPreferences.preferredStyle)
    }
    var musicEnabled by remember {
        mutableStateOf(initialPreferences.musicEnabled)
    }
    var spiritualLanguage by remember {
        mutableStateOf(initialPreferences.spiritualLanguage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = "Profil & Präferenzen",
            subtitle = "Lege fest, wie die App Meditationen für dich bevorzugt strukturieren soll."
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
                    value = preferredDuration,
                    onValueChange = { preferredDuration = it },
                    label = { Text("Bevorzugte Dauer (Minuten)") },
                    modifier = Modifier.fillMaxSize()
                )

                OutlinedTextField(
                    value = preferredTone,
                    onValueChange = { preferredTone = it },
                    label = { Text("Bevorzugte Tonalität") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = preferredStyle,
                    onValueChange = { preferredStyle = it },
                    label = { Text("Bevorzugter Stil") },
                    modifier = Modifier.padding(top = 12.dp)
                )

                Text(
                    text = "Musik standardmäßig aktiv",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Checkbox(
                    checked = musicEnabled,
                    onCheckedChange = { musicEnabled = it }
                )

                Text(
                    text = "Spirituelle Sprache erlauben",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Checkbox(
                    checked = spiritualLanguage,
                    onCheckedChange = { spiritualLanguage = it }
                )

                PrimaryButton(
                    text = "Präferenzen speichern",
                    onClick = {
                        onSave(
                            UserPreferences(
                                preferredDurationMinutes = preferredDuration.toIntOrNull() ?: 10,
                                preferredTone = preferredTone,
                                preferredStyle = preferredStyle,
                                musicEnabled = musicEnabled,
                                spiritualLanguage = spiritualLanguage
                            )
                        )
                    },
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    }
}