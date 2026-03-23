package com.example.meditationbio.features

data class MeditationFeatures(
    val dataQualityScore: Int,
    val ibiQualityScore: Int,

    val motionScore: Double,
    val stillnessScore: Int,

    val heartRateCurrent: Int,
    val heartRateTrend30s: Double,

    val ibiAvgMs: Double,
    val rmssd: Double,
    val rmssdTrend: String,

    val stabilityScore: Int
)