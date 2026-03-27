package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.UserPreferences

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
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Profil & Präferenzen")

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        OutlinedTextField(
            value = preferredDuration,
            onValueChange = { preferredDuration = it },
            label = { Text("Bevorzugte Dauer (Minuten)") },
            modifier = Modifier.padding(top = 16.dp)
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
            modifier = Modifier.padding(top = 16.dp)
        )
        Checkbox(
            checked = musicEnabled,
            onCheckedChange = { musicEnabled = it }
        )

        Text(
            text = "Spirituelle Sprache erlauben",
            modifier = Modifier.padding(top = 16.dp)
        )
        Checkbox(
            checked = spiritualLanguage,
            onCheckedChange = { spiritualLanguage = it }
        )

        Button(
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
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Präferenzen speichern")
        }
    }
}