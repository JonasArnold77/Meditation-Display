package com.example.meditationbio.core

class MeditationSessionManager {

    var isRunning: Boolean = false
        private set

    var sessionId: String = ""
        private set

    var startedAtMillis: Long = 0L
        private set

    var activeModeId: String = ""
        private set

    fun startSession(modeId: String): String {
        sessionId = "session_${System.currentTimeMillis()}"
        startedAtMillis = System.currentTimeMillis()
        activeModeId = modeId
        isRunning = true
        return sessionId
    }

    fun stopSession() {
        isRunning = false
    }

    fun elapsedSec(): Int {
        if (!isRunning || startedAtMillis == 0L) return 0
        return ((System.currentTimeMillis() - startedAtMillis) / 1000L).toInt()
    }
}