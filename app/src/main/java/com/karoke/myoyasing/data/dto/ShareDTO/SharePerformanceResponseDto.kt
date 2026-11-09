package com.karoke.myoyasing.data.dto.ShareDTO

import com.google.gson.annotations.SerializedName

data class SharePerformanceResponseDto(

    @SerializedName("performanceId")
    val performanceId: Long,

    @SerializedName("shareUrl")
    val shareUrl: String,

    @SerializedName("mediaUrl")
    val mediaUrl: String,

    @SerializedName("message")
    val message: String
)