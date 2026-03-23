package com.example.meditationbio.breath

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class BreathCuePlayer(context: Context) : TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.GERMAN
            tts.setSpeechRate(0.9f)
            isReady = true
        }
    }

    fun speakPhase(phase: BreathPhase) {
        if (!isReady) return

        val text = when (phase) {
            BreathPhase.INHALE -> "Ein"
            BreathPhase.HOLD_IN -> "Halten"
            BreathPhase.EXHALE -> "Aus"
            BreathPhase.HOLD_OUT -> "Halten"
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "breath_phase")
    }

    fun speakCoachText(text: String) {
        if (!isReady) return
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "coach_text")
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}