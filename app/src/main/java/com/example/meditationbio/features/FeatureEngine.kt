package com.example.meditationbio.features

import com.example.meditationbio.sensors.SensorSample
import kotlin.math.abs
import kotlin.math.sqrt

class FeatureEngine {

    fun calculate(samples: List<SensorSample>): MeditationFeatures {
        if (samples.isEmpty()) {
            return MeditationFeatures(
                dataQualityScore = 0,
                ibiQualityScore = 0,
                motionScore = 100.0,
                stillnessScore = 0,
                heartRateCurrent = 0,
                heartRateTrend30s = 0.0,
                ibiAvgMs = 0.0,
                rmssd = 0.0,
                rmssdTrend = "unknown",
                stabilityScore = 0
            )
        }

        val latest = samples.last()

        val motionValues = samples.map {
            val accelMag = magnitude(it.accelX, it.accelY, it.accelZ)
            val gyroMag = magnitude(it.gyroX, it.gyroY, it.gyroZ)
            abs(accelMag - 9.81) + gyroMag
        }

        val avgMotion = motionValues.average()
        val stillnessScore = (100.0 - (avgMotion * 20.0)).coerceIn(0.0, 100.0).toInt()

        val hrValues = samples.map { it.heartRateBpm }.filter { it > 0 }
        val heartRateCurrent = hrValues.lastOrNull() ?: 0
        val heartRateTrend30s = if (hrValues.size >= 2) {
            hrValues.takeLast(minOf(15, hrValues.size)).average() -
                    hrValues.take(minOf(15, hrValues.size)).average()
        } else {
            0.0
        }

        val allIbi = samples.flatMap { it.ibiMs }.filter { it > 0 }
        val ibiAvg = if (allIbi.isNotEmpty()) allIbi.average() else 0.0
        val rmssdValue = rmssd(allIbi)

        val validIbiStatuses = samples.flatMap { it.ibiStatus }
        val goodIbiCount = validIbiStatuses.count { it >= 0 }
        val ibiQualityScore = if (validIbiStatuses.isNotEmpty()) {
            ((goodIbiCount.toDouble() / validIbiStatuses.size.toDouble()) * 100.0).toInt()
        } else {
            0
        }

        val dataQualityScore = ((stillnessScore + ibiQualityScore) / 2.0).toInt()

        val stabilityScore = ((stillnessScore + ibiQualityScore + qualityFromHr(latest.heartRateStatus)) / 3.0).toInt()

        return MeditationFeatures(
            dataQualityScore = dataQualityScore,
            ibiQualityScore = ibiQualityScore,
            motionScore = avgMotion,
            stillnessScore = stillnessScore,
            heartRateCurrent = heartRateCurrent,
            heartRateTrend30s = heartRateTrend30s,
            ibiAvgMs = ibiAvg,
            rmssd = rmssdValue,
            rmssdTrend = "stable",
            stabilityScore = stabilityScore
        )
    }

    private fun magnitude(x: Double, y: Double, z: Double): Double {
        return sqrt(x * x + y * y + z * z)
    }

    private fun rmssd(values: List<Int>): Double {
        if (values.size < 2) return 0.0
        val diffsSquared = mutableListOf<Double>()
        for (i in 1 until values.size) {
            val diff = (values[i] - values[i - 1]).toDouble()
            diffsSquared.add(diff * diff)
        }
        return sqrt(diffsSquared.average())
    }

    private fun qualityFromHr(status: Int): Int {
        return if (status >= 0) 100 else 30
    }
}