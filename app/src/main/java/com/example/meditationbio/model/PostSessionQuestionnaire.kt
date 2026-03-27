package com.example.meditationbio.model

data class PostSessionQuestionnaire(
    val calmnessNow: Int = 5,
    val relief: Int = 5,
    val focusNow: Int = 5,
    val helpfulness: Int = 5,
    val wantSimilarMeditations: Boolean = true
)