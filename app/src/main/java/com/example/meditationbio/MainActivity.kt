package com.example.meditationbio

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meditationbio.breath.BreathCuePlayer
import com.example.meditationbio.breath.BreathPacerEngine
import com.example.meditationbio.breath.BreathPhase
import com.example.meditationbio.core.AppRuntimeStore
import com.example.meditationbio.core.MeditationDirector
import com.example.meditationbio.core.MeditationSessionManager
import com.example.meditationbio.features.FeatureEngine
import com.example.meditationbio.modes.BreathworkMode
import com.example.meditationbio.state.MeditationState
import com.example.meditationbio.state.MeditationStateEngine

class MainActivity : ComponentActivity() {

    companion object {
        var latestPayload by mutableStateOf("Noch keine Daten von der Uhr empfangen.")

        var sessionStatusText by mutableStateOf("Session: gestoppt")
        var sessionIdText by mutableStateOf("Session-ID: -")
        var modeText by mutableStateOf("Mode: -")

        var phaseText by mutableStateOf("Phase: -")
        var patternText by mutableStateOf("Pattern: -")

        var featureText by mutableStateOf("Features: -")
        var stateText by mutableStateOf("State: -")
        var actionText by mutableStateOf("Aktion: -")
    }

    private lateinit var cuePlayer: BreathCuePlayer
    private lateinit var pacerEngine: BreathPacerEngine
    private lateinit var sessionManager: MeditationSessionManager
    private lateinit var featureEngine: FeatureEngine
    private lateinit var stateEngine: MeditationStateEngine
    private lateinit var director: MeditationDirector

    private val handler = Handler(Looper.getMainLooper())
    private var loopRunning = false

    private val updateLoop = object : Runnable {
        override fun run() {
            if (!loopRunning) return

            if (sessionManager.isRunning) {
                val samples = AppRuntimeStore.sensorBuffer.getLast(30)
                val features = featureEngine.calculate(samples)
                val state = stateEngine.resolve(features, sessionManager.elapsedSec())
                val response = director.decide(
                    features = features,
                    state = state,
                    sessionId = sessionManager.sessionId,
                    elapsedSec = sessionManager.elapsedSec()
                )

                featureText =
                    "Qualität=${features.dataQualityScore}, IBI=${features.ibiQualityScore}, " +
                            "Stillness=${features.stillnessScore}, HR=${features.heartRateCurrent}, " +
                            "HR-Trend=${"%.1f".format(features.heartRateTrend30s)}, RMSSD=${"%.1f".format(features.rmssd)}"

                stateText = "State: ${state.toReadable()}"
                actionText = "Aktion: ${response.actionLabel ?: "-"}"

                response.nextBreathPattern?.let { pattern ->
                    pacerEngine.updatePattern(pattern)
                    patternText = "Pattern: ${pattern.label()}"
                }

                if (response.speakNow && !response.spokenText.isNullOrBlank()) {
                    cuePlayer.speakCoachText(response.spokenText)
                }
            }

            handler.postDelayed(this, 500L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cuePlayer = BreathCuePlayer(this)
        sessionManager = MeditationSessionManager()
        featureEngine = FeatureEngine()
        stateEngine = MeditationStateEngine()
        director = MeditationDirector(BreathworkMode())

        pacerEngine = BreathPacerEngine(
            cuePlayer = cuePlayer,
            onSecondTick = { phase, secondInPhase, totalPhaseSec ->
                phaseText = "Phase: ${phase.toReadable()} ${secondInPhase}/${totalPhaseSec}"
            },
            onPhaseChanged = { phase ->
                phaseText = "Phase: ${phase.toReadable()}"
            }
        )

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Meditation Bio",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = sessionStatusText,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text(
                        text = sessionIdText,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = modeText,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = patternText,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = phaseText,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = stateText,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text(
                        text = actionText,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = featureText,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = "Letzter Watch-Payload:",
                        modifier = Modifier.padding(top = 20.dp),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = latestPayload,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = {
                            val newSessionId = sessionManager.startSession("breathwork")
                            AppRuntimeStore.sensorBuffer.clear()

                            val startResponse = director.onSessionStart()

                            startResponse.nextBreathPattern?.let { pattern ->
                                pacerEngine.start(pattern)
                                patternText = "Pattern: ${pattern.label()}"
                            }

                            if (startResponse.speakNow && !startResponse.spokenText.isNullOrBlank()) {
                                cuePlayer.speakCoachText(startResponse.spokenText)
                            }

                            sessionStatusText = "Session aktiv"
                            sessionIdText = "Session-ID: $newSessionId"
                            modeText = "Mode: breathwork"
                            stateText = "State: -"
                            actionText = "Aktion: Start"
                            featureText = "Features: warten auf Watch-Daten"
                        },
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        Text("Meditation starten")
                    }

                    Button(
                        onClick = {
                            pacerEngine.stop()
                            sessionManager.stopSession()

                            val stopResponse = director.onSessionStop()
                            if (stopResponse.speakNow && !stopResponse.spokenText.isNullOrBlank()) {
                                cuePlayer.speakCoachText(stopResponse.spokenText)
                            }

                            sessionStatusText = "Session gestoppt"
                            phaseText = "Phase: -"
                            patternText = "Pattern: -"
                            stateText = "State: -"
                            actionText = "Aktion: Stop"
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Meditation stoppen")
                    }
                }
            }
        }

        startLoop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLoop()
        pacerEngine.stop()
        cuePlayer.shutdown()
    }

    private fun startLoop() {
        if (loopRunning) return
        loopRunning = true
        handler.post(updateLoop)
    }

    private fun stopLoop() {
        loopRunning = false
        handler.removeCallbacks(updateLoop)
    }
}

private fun BreathPhase.toReadable(): String {
    return when (this) {
        BreathPhase.INHALE -> "Einatmen"
        BreathPhase.HOLD_IN -> "Halten"
        BreathPhase.EXHALE -> "Ausatmen"
        BreathPhase.HOLD_OUT -> "Halten"
    }
}

private fun MeditationState.toReadable(): String {
    return when (this) {
        MeditationState.ARRIVING -> "Ankommen"
        MeditationState.SETTLING -> "Stabilisieren"
        MeditationState.STABLE -> "Stabil"
        MeditationState.DEEPENING -> "Vertiefen"
        MeditationState.DRIFTING -> "Abdriften"
        MeditationState.OVERLOADED -> "Überfordert"
        MeditationState.LOW_CONFIDENCE -> "Messung unsicher"
    }
}