package com.example.meditationbio.modes

import com.example.meditationbio.breath.BreathPattern
import com.example.meditationbio.features.MeditationFeatures
import com.example.meditationbio.state.MeditationState

class BreathworkMode : MeditationMode {

    override val id: String = "breathwork"

    override fun onSessionStart(): ModeResponse {
        return ModeResponse(
            speakNow = true,
            spokenText = "Wir beginnen sanft. Atme vier Sekunden ein und sechs Sekunden aus.",
            nextBreathPattern = BreathPattern(inhaleSec = 4, exhaleSec = 6),
            stateLabel = "start"
        )
    }

    override fun onSessionStop(): ModeResponse {
        return ModeResponse(
            speakNow = true,
            spokenText = "Die Atemsitzung ist beendet.",
            stateLabel = "stop"
        )
    }

    override fun decide(
        features: MeditationFeatures,
        state: MeditationState,
        context: MeditationModeContext
    ): ModeResponse {
        return when (state) {
            MeditationState.OVERLOADED -> ModeResponse(
                speakNow = true,
                spokenText = "Wir machen es etwas sanfter.",
                nextBreathPattern = BreathPattern(inhaleSec = 3, exhaleSec = 4),
                stateLabel = "overloaded",
                actionLabel = "gentler_pattern"
            )

            MeditationState.SETTLING -> ModeResponse(
                speakNow = false,
                nextBreathPattern = BreathPattern(inhaleSec = 4, exhaleSec = 5),
                stateLabel = "settling",
                actionLabel = "steady_pattern"
            )

            MeditationState.STABLE -> ModeResponse(
                speakNow = false,
                nextBreathPattern = BreathPattern(inhaleSec = 4, exhaleSec = 6),
                stateLabel = "stable",
                actionLabel = "keep_pattern"
            )

            MeditationState.DEEPENING -> ModeResponse(
                speakNow = true,
                spokenText = "Gut. Wir verlängern die Ausatmung leicht.",
                nextBreathPattern = BreathPattern(inhaleSec = 4, exhaleSec = 7),
                stateLabel = "deepening",
                actionLabel = "extend_exhale"
            )

            MeditationState.LOW_CONFIDENCE -> ModeResponse(
                speakNow = true,
                spokenText = "Die Messung ist gerade etwas unklar. Wir bleiben bei einem einfachen Rhythmus.",
                nextBreathPattern = BreathPattern(inhaleSec = 4, exhaleSec = 5),
                stateLabel = "low_confidence",
                actionLabel = "fallback_pattern"
            )

            MeditationState.ARRIVING -> ModeResponse(
                speakNow = false,
                nextBreathPattern = BreathPattern(inhaleSec = 4, exhaleSec = 5),
                stateLabel = "arriving",
                actionLabel = "start_settle"
            )

            MeditationState.DRIFTING -> ModeResponse(
                speakNow = true,
                spokenText = "Komm sanft wieder in den Rhythmus zurück.",
                nextBreathPattern = BreathPattern(inhaleSec = 4, exhaleSec = 5),
                stateLabel = "drifting",
                actionLabel = "return_to_pattern"
            )
        }
    }
}