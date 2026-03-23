package com.example.meditationbio.breath

data class BreathPattern(
    val inhaleSec: Int,
    val holdInSec: Int = 0,
    val exhaleSec: Int,
    val holdOutSec: Int = 0
) {
    fun label(): String {
        return if (holdInSec == 0 && holdOutSec == 0) {
            "${inhaleSec} ein / ${exhaleSec} aus"
        } else {
            "${inhaleSec} ein / ${holdInSec} halten / ${exhaleSec} aus / ${holdOutSec} halten"
        }
    }
}