package com.karoke.myoyasing.data.dto.hashtags

import com.google.gson.annotations.SerializedName

data class HashtagSearchRequest(
    @SerializedName("query")
    val query: String
)