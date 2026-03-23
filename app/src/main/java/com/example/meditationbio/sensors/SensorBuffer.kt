package com.example.meditationbio.sensors

class SensorBuffer(
    private val maxSize: Int = 300
) {
    private val samples = mutableListOf<SensorSample>()

    fun add(sample: SensorSample) {
        samples.add(sample)
        while (samples.size > maxSize) {
            samples.removeAt(0)
        }
    }

    fun getAll(): List<SensorSample> = samples.toList()

    fun getLast(seconds: Int): List<SensorSample> {
        if (samples.isEmpty()) return emptyList()
        val latest = samples.last().timestamp
        val cutoff = latest - (seconds * 1000L)
        return samples.filter { it.timestamp >= cutoff }
    }

    fun clear() {
        samples.clear()
    }
}