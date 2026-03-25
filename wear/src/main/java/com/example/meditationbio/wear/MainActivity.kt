package com.example.meditationbio.wear

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    companion object {
        var permissionStatus by mutableStateOf("Prüfe Berechtigungen ...")
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val allGranted = result.values.all { it }
            if (allGranted) {
                permissionStatus = "Berechtigungen erteilt"
                startStreamingService()
            } else {
                permissionStatus = "Nicht alle Berechtigungen erteilt"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ensurePermissionsAndStart()

        setContent {
            WearServiceScreen(
                permissionStatus = permissionStatus,
                onStopClick = {
                    val stopIntent = Intent(this, WearStreamingService::class.java).apply {
                        action = WearStreamingService.ACTION_STOP
                    }
                    startService(stopIntent)
                },
                onRetryClick = {
                    ensurePermissionsAndStart()
                }
            )
        }
    }

    private fun ensurePermissionsAndStart() {
        val permissions = mutableListOf<String>()

        // Klassische Sensor-Permission
        permissions.add(Manifest.permission.BODY_SENSORS)

        // Android 13-15: Background body sensors
        if (Build.VERSION.SDK_INT >= 33) {
            permissions.add("android.permission.BODY_SENSORS_BACKGROUND")
        }

        // Android 16+: granulare Heart-Rate-Permission
        if (Build.VERSION.SDK_INT >= 36) {
            permissions.add("android.permission.health.READ_HEART_RATE")
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isEmpty()) {
            permissionStatus = "Berechtigungen vorhanden"
            startStreamingService()
        } else {
            permissionStatus = "Fordere Berechtigungen an"
            permissionLauncher.launch(missing.toTypedArray())
        }
    }

    private fun startStreamingService() {
        val startIntent = Intent(this, WearStreamingService::class.java).apply {
            action = WearStreamingService.ACTION_START
        }
        startForegroundService(startIntent)
        permissionStatus = "Streaming-Service gestartet"
    }
}

@Composable
fun WearServiceScreen(
    permissionStatus: String,
    onStopClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Streaming läuft im Service")
        Text(
            text = permissionStatus,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = onRetryClick,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Berechtigungen prüfen")
        }

        Button(
            onClick = onStopClick,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Stop")
        }
    }
}