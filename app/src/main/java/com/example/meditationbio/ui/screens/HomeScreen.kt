package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SectionCard

@androidx.compose.runtime.Composable
fun HomeScreen(
    liveBioText: String,
    sendStatus: String,
    onOpenProblemFields: () -> Unit,
    onOpenEditor: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = "Meditation Bio",
            subtitle = "Persönliche Meditationen mit Biofeedback, Verlauf und intelligenten Empfehlungen."
        )

        SectionCard {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Starte dort, wo du gerade stehst.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Wähle ein Problemfeld, öffne den freien Editor oder schau in deinen Fortschritt.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PrimaryButton(
                    text = "Problemfelder",
                    onClick = onOpenProblemFields,
                    modifier = Modifier.padding(top = 20.dp)
                )

                PrimaryButton(
                    text = "Editor",
                    onClick = onOpenEditor,
                    modifier = Modifier.padding(top = 12.dp)
                )

                PrimaryButton(
                    text = "Fortschritt",
                    onClick = onOpenProgress,
                    modifier = Modifier.padding(top = 12.dp)
                )

                PrimaryButton(
                    text = "Profil",
                    onClick = onOpenProfile,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        SectionCard(
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = sendStatus,
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionCard(
            modifier = Modifier.padding(top = 20.dp)
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
    }
}