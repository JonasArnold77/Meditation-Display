package com.example.meditationbio.wear

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.Wearable
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class WearStreamingService : Service(), SensorEventListener {

    companion object {
        private const val TAG = "WearStreamingService"
        private const val CHANNEL_ID = "wear_stream_channel"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.meditationbio.wear.action.START"
        const val ACTION_STOP = "com.example.meditationbio.wear.action.STOP"
    }

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
    private var ibiList: List<Int> = emptyList()
    private var ibiStatusList: List<Int> = emptyList()

    private var lastTimestamp = 0L
    private var sampleCounter = 0
    private var currentSessionId: String = ""

    private val handler = Handler(Looper.getMainLooper())
    private var isStreaming = false
    private val sendIntervalMs = 1000L

    private var healthTrackingService: HealthTrackingService? = null
    private var heartRateTracker: HealthTracker? = null
    private var isHealthConnected = false

    private val connectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            isHealthConnected = true
            Log.d(TAG, "Health Platform verbunden")
            startHeartRateTracking()
        }

        override fun onConnectionEnded() {
            isHealthConnected = false
            Log.d(TAG, "Health Platform Verbindung beendet")
        }

        override fun onConnectionFailed(exception: HealthTrackerException) {
            isHealthConnected = false
            Log.e(TAG, "Health Platform Verbindung fehlgeschlagen", exception)
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

                    ibiList = try {
                        dataPoint.getValue(ValueKey.HeartRateSet.IBI_LIST)
                    } catch (_: Exception) {
                        emptyList()
                    }

                    ibiStatusList = try {
                        dataPoint.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST)
                    } catch (_: Exception) {
                        emptyList()
                    }

                    Log.d(TAG, "HR=$heartRate status=$heartRateStatus ibi=$ibiList ibiStatus=$ibiStatusList")
                } catch (e: Exception) {
                    Log.e(TAG, "Lesen von HR/IBI fehlgeschlagen", e)
                }
            }
        }

        override fun onError(trackerError: HealthTracker.TrackerError) {
            Log.e(TAG, "HeartRate Tracker Error: $trackerError")
        }

        override fun onFlushCompleted() {
            Log.d(TAG, "HeartRate Flush completed")
        }
    }

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        createNotificationChannel()
        Log.d(TAG, "Service erstellt")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopStreaming()
                stopSelf()
                return START_NOT_STICKY
            }

            ACTION_START, null -> {
                startForeground(NOTIFICATION_ID, buildNotification())
                startStreaming()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStreaming()
        stopHeartRateTracking()
        try {
            healthTrackingService?.disconnectService()
        } catch (e: Exception) {
            Log.e(TAG, "disconnectService fehlgeschlagen", e)
        }
        Log.d(TAG, "Service zerstört")
    }

    override fun onBind(intent: Intent?): IBinder? = null

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

        Log.d(
            TAG,
            String.format(
                Locale.US,
                "A %.1f %.1f %.1f / G %.1f %.1f %.1f",
                accelX, accelY, accelZ, gyroX, gyroY, gyroZ
            )
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Accuracy geändert: sensor=${sensor?.name}, accuracy=$accuracy")
    }

    private fun startStreaming() {
        if (isStreaming) return

        currentSessionId = "session_${System.currentTimeMillis()}"
        sampleCounter = 0
        isStreaming = true

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Accelerometer registriert")
        }

        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Gyroskop registriert")
        }

        ensureHeartRatePermissionAndConnect()

        handler.post(sendRunnable)
        Log.d(TAG, "Streaming gestartet: $currentSessionId")
    }

    private fun stopStreaming() {
        if (!isStreaming) return

        isStreaming = false
        handler.removeCallbacks(sendRunnable)
        sensorManager.unregisterListener(this)

        Log.d(TAG, "Streaming gestoppt: $currentSessionId")
    }

    private fun ensureHeartRatePermissionAndConnect() {
        val bodySensorsGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED

        val readHeartRateGranted =
            if (Build.VERSION.SDK_INT >= 36) {
                ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.health.READ_HEART_RATE"
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        if ((bodySensorsGranted || readHeartRateGranted) && !isHealthConnected) {
            connectToHealthPlatform()
        } else {
            Log.w(TAG, "Keine ausreichende Heart-Rate-Permission vorhanden.")
        }
    }

    private fun connectToHealthPlatform() {
        try {
            healthTrackingService = HealthTrackingService(connectionListener, this)
            healthTrackingService?.connectService()
            Log.d(TAG, "Verbindung zur Health Platform angefordert")
        } catch (e: Exception) {
            Log.e(TAG, "HealthTrackingService Fehler", e)
        }
    }

    private fun startHeartRateTracking() {
        val service = healthTrackingService ?: return

        try {
            val trackers = service.trackingCapability.supportHealthTrackerTypes
            if (!trackers.contains(HealthTrackerType.HEART_RATE_CONTINUOUS)) {
                Log.e(TAG, "HEART_RATE_CONTINUOUS nicht unterstützt")
                return
            }

            heartRateTracker?.unsetEventListener()
            heartRateTracker = service.getHealthTracker(HealthTrackerType.HEART_RATE_CONTINUOUS)
            heartRateTracker?.setEventListener(heartRateListener)

            Log.d(TAG, "Heart Rate Tracking gestartet")
        } catch (e: HealthTrackerException) {
            Log.e(TAG, "Tracker-Fehler", e)
        } catch (e: Exception) {
            Log.e(TAG, "Start fehlgeschlagen", e)
        }
    }

    private fun stopHeartRateTracking() {
        try {
            heartRateTracker?.unsetEventListener()
            heartRateTracker = null
            Log.d(TAG, "Heart Rate Tracking gestoppt")
        } catch (e: Exception) {
            Log.e(TAG, "Stop fehlgeschlagen", e)
        }
    }

    private fun sendSensorJson() {
        if (lastTimestamp == 0L) {
            Log.d(TAG, "Noch keine Sensordaten vorhanden")
            return
        }

        sampleCounter++

        val ibiJsonArray = JSONArray()
        ibiList.forEach { ibiJsonArray.put(it) }

        val ibiStatusJsonArray = JSONArray()
        ibiStatusList.forEach { ibiStatusJsonArray.put(it) }

        val json = JSONObject().apply {
            put("type", "sensor_sample")
            put("sessionId", currentSessionId)
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
                    put("ibiMs", ibiJsonArray)
                    put("ibiStatus", ibiStatusJsonArray)
                }
            )
        }

        val payload = json.toString().toByteArray(Charsets.UTF_8)

        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                Log.d(TAG, "Gefundene Nodes: ${nodes.size}")

                if (nodes.isEmpty()) {
                    Log.d(TAG, "Kein verbundenes Handy gefunden")
                }

                nodes.forEach { node ->
                    Wearable.getMessageClient(this)
                        .sendMessage(node.id, "/bio", payload)
                        .addOnSuccessListener {
                            Log.d(TAG, "Sensor-JSON erfolgreich gesendet")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Senden fehlgeschlagen: ${e.message}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "connectedNodes fehlgeschlagen", e)
            }
    }

    private fun buildNotification(): Notification {
        val openIntent = Intent(this, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, WearStreamingService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Meditation Bio")
            .setContentText("Streaming läuft")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(openPendingIntent)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Wear Streaming",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}