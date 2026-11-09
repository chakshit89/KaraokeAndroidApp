package com.karoke.myoyasing.data.dto.hashtags

import com.google.gson.annotations.SerializedName

data class HashtagDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)