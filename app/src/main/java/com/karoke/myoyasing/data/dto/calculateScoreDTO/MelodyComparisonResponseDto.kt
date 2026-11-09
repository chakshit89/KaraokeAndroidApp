package com.karoke.myoyasing.data.dto.calculateScoreDTO

import com.google.gson.annotations.SerializedName

data class MelodyComparisonResponseDto(

    @SerializedName("songId")
    val songId: Long,

    @SerializedName("songTitle")
    val songTitle: String,

    @SerializedName("pitchScore")
    val pitchScore: Double,

    @SerializedName("timingScore")
    val timingScore: Double,

    @SerializedName("completenessScore")
    val completenessScore: Double,

    @SerializedName("finalScore")
    val finalScore: Double,

    @SerializedName("grade")
    val grade: String,

    @SerializedName("message")
    val message: String
)