package com.karoke.myoyasing.domain.model

data class MelodyComparisonResult(

    val songId: Long,

    val songTitle: String,

    val pitchScore: Double,

    val timingScore: Double,

    val completenessScore: Double,

    val finalScore: Double,

    val message: String,
    val grade: String
)