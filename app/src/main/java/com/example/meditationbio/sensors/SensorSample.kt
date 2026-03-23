package com.example.meditationbio.sensors

data class SensorSample(
    val sessionId: String,
    val sampleIndex: Int,
    val timestamp: Long,

    val accelX: Double,
    val accelY: Double,
    val accelZ: Double,

    val gyroX: Double,
    val gyroY: Double,
    val gyroZ: Double,

    val heartRateBpm: Int,
    val heartRateStatus: Int,
    val ibiMs: List<Int>,
    val ibiStatus: List<Int>
)