package com.karoke.myoyasing.data.dto.CommentsDTO

import com.google.gson.annotations.SerializedName

data class PerformanceCommentDto(

    @SerializedName("commentId")
    val commentId: Long,

    @SerializedName("performanceId")
    val performanceId: Long,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("createdAt")
    val createdAt: String
)