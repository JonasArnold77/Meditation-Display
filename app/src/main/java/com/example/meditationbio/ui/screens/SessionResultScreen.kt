package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.MeditationEffectiveness
import com.example.meditationbio.ui.components.InfoBadge
import com.example.meditationbio.ui.components.MetricCard
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SectionCard

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
        ScreenHeader(
            title = "Session-Ergebnis",
            subtitle = "Eine erste Einschätzung basierend auf Fragebogen und Blutdruck."
        )

        SectionCard {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "${effectiveness?.score ?: 0}/100",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(modifier = Modifier.padding(top = 12.dp)) {
                    InfoBadge(text = "Wirksamkeit")
                }

                Text(
                    text = effectiveness?.summary ?: "-",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        MetricCard(
            title = "Subjektive Veränderung",
            value = "${effectiveness?.subjectiveImprovement ?: 0}",
            modifier = Modifier.padding(top = 16.dp)
        )

        MetricCard(
            title = "Δ Systolisch",
            value = "${effectiveness?.bloodPressureDeltaSystolic ?: 0}",
            modifier = Modifier.padding(top = 16.dp)
        )

        MetricCard(
            title = "Δ Diastolisch",
            value = "${effectiveness?.bloodPressureDeltaDiastolic ?: 0}",
            modifier = Modifier.padding(top = 16.dp)
        )

        PrimaryButton(
            text = "Weitere Empfehlung ansehen",
            onClick = onOpenProblemFields,
            modifier = Modifier.padding(top = 20.dp)
        )

        PrimaryButton(
            text = "Zur Startseite",
            onClick = onContinue,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}