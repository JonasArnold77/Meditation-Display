package com.example.meditationbio.wear

import android.content.Context
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.Wearable
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

    private var lastTimestamp = 0L
    private var sampleCounter = 0

    private val handler = Handler(Looper.getMainLooper())
    private var isStreaming = false
    private val sendIntervalMs = 1000L

    companion object {
        var sensorText by mutableStateOf("Noch keine Sensordaten")
        var streamStatus by mutableStateOf("Streaming gestoppt")
    }

    private val sendRunnable = object : Runnable {
        override fun run() {
            if (isStreaming) {
                sendSensorJson()
                handler.postDelayed(this, sendIntervalMs)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            WearSensorScreen(
                sensorText = sensorText,
                streamStatus = streamStatus,
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
        } ?: Log.e("WearSensor", "Kein Accelerometer gefunden")

        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("WearSensor", "Gyroskop registriert")
        } ?: Log.e("WearSensor", "Kein Gyroskop gefunden")

        if (accelerometer == null && gyroscope == null) {
            sensorText = "Keine Sensoren gefunden"
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        stopStreaming()
        Log.d("WearSensor", "Sensor Listener entfernt")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStreaming()
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
            "Acc x:%.2f y:%.2f z:%.2f\nGyro x:%.2f y:%.2f z:%.2f",
            accelX, accelY, accelZ,
            gyroX, gyroY, gyroZ
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("WearSensor", "Accuracy geändert: sensor=${sensor?.name}, accuracy=$accuracy")
    }

    private fun startStreaming() {
        if (isStreaming) return

        isStreaming = true
        streamStatus = "Streaming aktiv (1s)"
        Log.d("WearStream", "Streaming gestartet")

        handler.post(sendRunnable)
    }

    private fun stopStreaming() {
        if (!isStreaming) return

        isStreaming = false
        streamStatus = "Streaming gestoppt"
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
    sensorText: String,
    streamStatus: String,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(streamStatus)

        Text(
            text = sensorText,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = onStartClick,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text("Start")
        }

        Button(
            onClick = onStopClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Stop")
        }
    }
}