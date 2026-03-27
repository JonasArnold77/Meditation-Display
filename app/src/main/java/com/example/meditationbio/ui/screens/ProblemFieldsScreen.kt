package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.ProblemField

@androidx.compose.runtime.Composable
fun ProblemFieldsScreen(
    problemFields: List<ProblemField>,
    onBack: () -> Unit,
    onProblemFieldSelected: (ProblemField) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Problemfelder")

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        problemFields.forEach { field ->
            Button(
                onClick = { onProblemFieldSelected(field) },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(field.title)
            }

            Text(
                text = field.subtitle,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = field.description,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}