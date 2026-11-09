package com.karoke.myoyasing.data.dto.CreatePerformanceDTO

data class CreatePerformanceRequestDTO(
    val audioUrl: String,
    val grade: String,
    val pitchAccuracy: Int,
    val score: Int,
    val songId: Int,
    val timingAccuracy: Int,
    val videoUrl: String?
)