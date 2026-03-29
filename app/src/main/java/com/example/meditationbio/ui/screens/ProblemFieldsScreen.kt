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
import com.example.meditationbio.model.ProblemField
import com.example.meditationbio.ui.components.PrimaryButton
import com.example.meditationbio.ui.components.ScreenHeader
import com.example.meditationbio.ui.components.SecondaryButton
import com.example.meditationbio.ui.components.SectionCard

@androidx.compose.runtime.Composable
fun ProblemFieldsScreen(
    problemFields: List<ProblemField>,
    onBack: () -> Unit,
    onProblemFieldSelected: (ProblemField) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = "Problemfelder",
            subtitle = "Wähle den Bereich, in dem dich die App gezielt unterstützen soll."
        )

        SecondaryButton(
            text = "Zurück",
            onClick = onBack
        )

        problemFields.forEach { field ->
            SectionCard(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = field.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = field.subtitle,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = field.description,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    PrimaryButton(
                        text = "Auswählen",
                        onClick = { onProblemFieldSelected(field) },
                        modifier = Modifier.padding(top = 18.dp)
                    )
                }
            }
        }
    }
}