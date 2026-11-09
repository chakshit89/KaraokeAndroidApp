package com.karoke.myoyasing.domain.model

data class PerformanceComment(

    val commentId: Long,

    val performanceId: Long,

    val userId: Long,

    val username: String,

    val content: String,

    val createdAt: String
)