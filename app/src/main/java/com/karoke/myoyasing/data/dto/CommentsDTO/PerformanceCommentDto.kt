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
    val username: String? = null,

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("createdAt")
    val createdAt: String
)
