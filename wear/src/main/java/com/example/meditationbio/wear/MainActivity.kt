package com.example.meditationbio.wear

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.google.android.gms.wearable.Wearable
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import org.json.JSONObject
import java.util.Locale

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private var accelX = 0f
    private var accelY = 0f
    private var accelZ = 0f

    private var gyroX = 0f
    private var gyroY = 0f
    private var gyroZ = 0f

    private var heartRate = 0
    private var heartRateStatus = -1

    private var lastTimestamp = 0L
    private var sampleCounter = 0

    private val handler = Handler(Looper.getMainLooper())
    private var isStreaming = false
    private val sendIntervalMs = 1000L

    private var healthTrackingService: HealthTrackingService? = null
    private var heartRateTracker: HealthTracker? = null
    private var isHealthConnected = false

    companion object {
        var streamStatus by mutableStateOf("Stream: Stop")
        var healthStatusText by mutableStateOf("Health: -")
        var heartRateText by mutableStateOf("HR: -")
        var sensorText by mutableStateOf("Acc:-  Gyro:-")
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                healthStatusText = "Health: OK"
                connectToHealthPlatform()
            } else {
                healthStatusText = "Health: NoPerm"
            }
        }

    private val connectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            isHealthConnected = true
            healthStatusText = "Health: Verbunden"
            Log.d("HeartRate", "Health Platform verbunden")
            startHeartRateTracking()
        }

        override fun onConnectionEnded() {
            isHealthConnected = false
            healthStatusText = "Health: Getrennt"
            Log.d("HeartRate", "Health Platform Verbindung beendet")
        }

        override fun onConnectionFailed(exception: HealthTrackerException) {
            isHealthConnected = false
            healthStatusText = "Health: Fehler"
            Log.e("HeartRate", "Verbindung fehlgeschlagen", exception)
        }
    }

    private val sendRunnable = object : Runnable {
        override fun run() {
            if (isStreaming) {
                sendSensorJson()
                handler.postDelayed(this, sendIntervalMs)
            }
        }
    }

    private val heartRateListener = object : HealthTracker.TrackerEventListener {
        override fun onDataReceived(dataPoints: List<DataPoint>) {
            for (dataPoint in dataPoints) {
                try {
                    heartRateStatus = dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS)
                    heartRate = dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE)
                    heartRateText = "HR: $heartRate"
                    Log.d("HeartRate", "Heart rate: $heartRate, status=$heartRateStatus")
                } catch (e: Exception) {
                    Log.e("HeartRate", "Lesen der HR fehlgeschlagen", e)
                }
            }
        }

        override fun onError(trackerError: HealthTracker.TrackerError) {
            healthStatusText = "Health: Error"
            Log.e("HeartRate", "Tracker Error: $trackerError")
        }

        override fun onFlushCompleted() {
            Log.d("HeartRate", "Flush completed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            WearSensorScreen(
                streamStatus = streamStatus,
                healthStatusText = healthStatusText,
                heartRateText = heartRateText,
                sensorText = sensorText,
                onStartClick = { startStreaming() },
                onStopClick = { stopStreaming() }
            )
        }
    }

    override fun onResume() {
        super.onResume()

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("WearSensor", "Accelerometer registriert")
        }

        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("WearSensor", "Gyroskop registriert")
        }

        ensureHeartRatePermissionAndConnect()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        stopStreaming()
        stopHeartRateTracking()
        Log.d("WearSensor", "Sensor Listener entfernt")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStreaming()
        stopHeartRateTracking()
        try {
            healthTrackingService?.disconnectService()
        } catch (e: Exception) {
            Log.e("HeartRate", "disconnectService fehlgeschlagen", e)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelX = event.values[0]
                accelY = event.values[1]
                accelZ = event.values[2]
                lastTimestamp = System.currentTimeMillis()
            }

            Sensor.TYPE_GYROSCOPE -> {
                gyroX = event.values[0]
                gyroY = event.values[1]
                gyroZ = event.values[2]
                lastTimestamp = System.currentTimeMillis()
            }
        }

        sensorText = String.format(
            Locale.US,
            "A %.1f %.1f %.1f\nG %.1f %.1f %.1f",
            accelX, accelY, accelZ,
            gyroX, gyroY, gyroZ
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("WearSensor", "Accuracy geändert: sensor=${sensor?.name}, accuracy=$accuracy")
    }

    private fun ensureHeartRatePermissionAndConnect() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (!isHealthConnected) {
                connectToHealthPlatform()
            }
        } else {
            permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        }
    }

    private fun connectToHealthPlatform() {
        try {
            healthTrackingService = HealthTrackingService(connectionListener, this)
            healthTrackingService?.connectService()
            healthStatusText = "Health: Verbinde"
            Log.d("HeartRate", "Verbindung zur Health Platform angefordert")
        } catch (e: Exception) {
            healthStatusText = "Health: Fehler"
            Log.e("HeartRate", "HealthTrackingService Fehler", e)
        }
    }

    private fun startHeartRateTracking() {
        val service = healthTrackingService ?: return

        try {
            val trackers = service.trackingCapability.supportHealthTrackerTypes
            if (!trackers.contains(HealthTrackerType.HEART_RATE_CONTINUOUS)) {
                healthStatusText = "Health: Kein HR"
                Log.e("HeartRate", "HEART_RATE_CONTINUOUS nicht unterstützt")
                return
            }

            heartRateTracker?.unsetEventListener()
            heartRateTracker = service.getHealthTracker(HealthTrackerType.HEART_RATE_CONTINUOUS)
            heartRateTracker?.setEventListener(heartRateListener)

            healthStatusText = "Health: HR aktiv"
            Log.d("HeartRate", "Heart Rate Tracking gestartet")
        } catch (e: HealthTrackerException) {
            healthStatusText = "Health: Fehler"
            Log.e("HeartRate", "Tracker-Fehler", e)
        } catch (e: Exception) {
            healthStatusText = "Health: Fehler"
            Log.e("HeartRate", "Start fehlgeschlagen", e)
        }
    }

    private fun stopHeartRateTracking() {
        try {
            heartRateTracker?.unsetEventListener()
            heartRateTracker = null
            Log.d("HeartRate", "Heart Rate Tracking gestoppt")
        } catch (e: Exception) {
            Log.e("HeartRate", "Stop fehlgeschlagen", e)
        }
    }

    private fun startStreaming() {
        if (isStreaming) return

        isStreaming = true
        streamStatus = "Stream: Start"
        Log.d("WearStream", "Streaming gestartet")

        handler.post(sendRunnable)
    }

    private fun stopStreaming() {
        if (!isStreaming) return

        isStreaming = false
        streamStatus = "Stream: Stop"
        handler.removeCallbacks(sendRunnable)
        Log.d("WearStream", "Streaming gestoppt")
    }

    private fun sendSensorJson() {
        if (lastTimestamp == 0L) {
            Log.d("WearSend", "Noch keine Sensordaten vorhanden")
            return
        }

        sampleCounter++

        val json = JSONObject().apply {
            put("type", "sensor_sample")
            put("sessionId", "test_session_001")
            put("sampleIndex", sampleCounter)
            put("timestamp", lastTimestamp)
            put("source", "galaxy_watch_4")

            put(
                "accelerometer",
                JSONObject().apply {
                    put("x", accelX.toDouble())
                    put("y", accelY.toDouble())
                    put("z", accelZ.toDouble())
                }
            )

            put(
                "gyroscope",
                JSONObject().apply {
                    put("x", gyroX.toDouble())
                    put("y", gyroY.toDouble())
                    put("z", gyroZ.toDouble())
                }
            )

            put(
                "heartRate",
                JSONObject().apply {
                    put("bpm", heartRate)
                    put("status", heartRateStatus)
                }
            )
        }

        val payload = json.toString().toByteArray(Charsets.UTF_8)

        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                Log.d("WearSend", "Gefundene Nodes: ${nodes.size}")

                if (nodes.isEmpty()) {
                    Log.d("WearSend", "Kein verbundenes Handy gefunden")
                }

                nodes.forEach { node ->
                    Log.d("WearSend", "Sende Sensor-JSON an ${node.displayName}: $json")

                    Wearable.getMessageClient(this)
                        .sendMessage(node.id, "/bio", payload)
                        .addOnSuccessListener {
                            Log.d("WearSend", "Sensor-JSON erfolgreich gesendet")
                        }
                        .addOnFailureListener { e ->
                            Log.e("WearSend", "Senden fehlgeschlagen: ${e.message}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("WearSend", "connectedNodes fehlgeschlagen: ${e.message}", e)
            }
    }
}

@Composable
fun WearSensorScreen(
    streamStatus: String,
    healthStatusText: String,
    heartRateText: String,
    sensorText: String,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(streamStatus)
        Text(healthStatusText)
        Text(heartRateText)
        Text(sensorText)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onStartClick) {
            Text("Start")
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(onClick = onStopClick) {
            Text("Stop")
        }
    }
}