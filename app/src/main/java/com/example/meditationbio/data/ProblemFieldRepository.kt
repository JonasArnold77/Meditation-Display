package com.example.meditationbio.data

import com.example.meditationbio.model.ProblemField

object ProblemFieldRepository {

    fun getAll(): List<ProblemField> {
        return listOf(
            ProblemField(
                id = "stress",
                title = "Stress",
                subtitle = "Akute Überlastung und Anspannung",
                description = "Meditationen zur Beruhigung, Entlastung und Stabilisierung."
            ),
            ProblemField(
                id = "anxiety",
                title = "Angst",
                subtitle = "Innere Unsicherheit und Nervosität",
                description = "Meditationen für Sicherheit, Erdung und sanfte Regulation."
            ),
            ProblemField(
                id = "sleep",
                title = "Schlaf",
                subtitle = "Einschlafen und nächtliche Unruhe",
                description = "Meditationen zum Runterfahren und Einschlafen."
            ),
            ProblemField(
                id = "overthinking",
                title = "Grübeln",
                subtitle = "Gedankenkarussell und mentale Unruhe",
                description = "Meditationen, die den Geist beruhigen und den Fokus zurückholen."
            ),
            ProblemField(
                id = "heartbreak",
                title = "Liebeskummer",
                subtitle = "Emotionale Belastung und Loslassen",
                description = "Meditationen für Trost, Selbstmitgefühl und emotionale Stabilisierung."
            ),
            ProblemField(
                id = "selfworth",
                title = "Selbstwert",
                subtitle = "Selbstkritik und innere Härte",
                description = "Meditationen zur Stärkung von Selbstmitgefühl und innerer Annahme."
            )
        )
    }
}