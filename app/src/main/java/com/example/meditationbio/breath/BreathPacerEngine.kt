package com.example.meditationbio.breath

import android.os.Handler
import android.os.Looper

class BreathPacerEngine(
    private val cuePlayer: BreathCuePlayer,
    private val onSecondTick: ((phase: BreathPhase, secondInPhase: Int, totalPhaseSec: Int) -> Unit)? = null,
    private val onPhaseChanged: ((phase: BreathPhase) -> Unit)? = null
) {
    private val handler = Handler(Looper.getMainLooper())

    private var currentPattern = BreathPattern(inhaleSec = 4, exhaleSec = 6)
    private var isRunning = false

    private var currentPhase = BreathPhase.INHALE
    private var currentSecondInPhase = 0
    private var currentPhaseDuration = currentPattern.inhaleSec

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return

            currentSecondInPhase++

            onSecondTick?.invoke(currentPhase, currentSecondInPhase, currentPhaseDuration)

            if (currentSecondInPhase >= currentPhaseDuration) {
                moveToNextPhase()
            }

            handler.postDelayed(this, 1000L)
        }
    }

    fun start(pattern: BreathPattern) {
        stop()

        currentPattern = pattern
        isRunning = true

        currentPhase = BreathPhase.INHALE
        currentSecondInPhase = 0
        currentPhaseDuration = currentPattern.inhaleSec

        cuePlayer.speakPhase(currentPhase)
        onPhaseChanged?.invoke(currentPhase)

        handler.postDelayed(tickRunnable, 1000L)
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(tickRunnable)
    }

    fun updatePattern(pattern: BreathPattern) {
        currentPattern = pattern
    }

    fun getCurrentPattern(): BreathPattern = currentPattern

    fun isActive(): Boolean = isRunning

    private fun moveToNextPhase() {
        currentPhase = when (currentPhase) {
            BreathPhase.INHALE -> {
                if (currentPattern.holdInSec > 0) BreathPhase.HOLD_IN else BreathPhase.EXHALE
            }
            BreathPhase.HOLD_IN -> BreathPhase.EXHALE
            BreathPhase.EXHALE -> {
                if (currentPattern.holdOutSec > 0) BreathPhase.HOLD_OUT else BreathPhase.INHALE
            }
            BreathPhase.HOLD_OUT -> BreathPhase.INHALE
        }

        currentSecondInPhase = 0
        currentPhaseDuration = when (currentPhase) {
            BreathPhase.INHALE -> currentPattern.inhaleSec
            BreathPhase.HOLD_IN -> currentPattern.holdInSec
            BreathPhase.EXHALE -> currentPattern.exhaleSec
            BreathPhase.HOLD_OUT -> currentPattern.holdOutSec
        }

        cuePlayer.speakPhase(currentPhase)
        onPhaseChanged?.invoke(currentPhase)
    }
}