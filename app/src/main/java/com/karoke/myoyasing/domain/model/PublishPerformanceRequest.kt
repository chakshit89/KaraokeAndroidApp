package com.karoke.myoyasing.domain.model

data class PublishPerformanceRequest(
    val description: String,
    val audience: String = "PUBLIC",
    val collaboratorIds: List<Int> = emptyList(),
    val hashtagIds: List<Int> = emptyList()
)