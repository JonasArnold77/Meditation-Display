package com.example.meditationbio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.model.BloodPressureMeasurement

@androidx.compose.runtime.Composable
fun BloodPressureInputScreen(
    title: String,
    initialValue: BloodPressureMeasurement,
    onBack: () -> Unit,
    onContinue: (BloodPressureMeasurement) -> Unit
) {
    var systolic by remember { mutableStateOf(initialValue.systolic.toString()) }
    var diastolic by remember { mutableStateOf(initialValue.diastolic.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(title)

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Zurück")
        }

        OutlinedTextField(
            value = systolic,
            onValueChange = { systolic = it },
            label = { Text("Systolisch") },
            modifier = Modifier.padding(top = 16.dp)
        )

        OutlinedTextField(
            value = diastolic,
            onValueChange = { diastolic = it },
            label = { Text("Diastolisch") },
            modifier = Modifier.padding(top = 12.dp)
        )

        Button(
            onClick = {
                onContinue(
                    BloodPressureMeasurement(
                        systolic = systolic.toIntOrNull() ?: 120,
                        diastolic = diastolic.toIntOrNull() ?: 80
                    )
                )
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Weiter")
        }
    }
}