package com.karoke.myoyasing.data.dto.publishPerformanceDTO

import com.google.gson.annotations.SerializedName

data class PublishPerformanceRequestDto(
    @SerializedName("description")
    val description: String,

    @SerializedName("audience")
    val audience: String = "PUBLIC",

    @SerializedName("collaboratorIds")
    val collaboratorIds: List<Int> = emptyList(),

    @SerializedName("hashtagIds")
    val hashtagIds: List<Int> = emptyList()
)