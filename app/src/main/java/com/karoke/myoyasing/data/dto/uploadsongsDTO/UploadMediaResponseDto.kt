package com.karoke.myoyasing.data.dto.uploadsongsDTO

import com.google.gson.annotations.SerializedName

data class UploadMediaResponseDto(
    @SerializedName("audioUrl")
    val audioUrl: String,

    @SerializedName("videoUrl")
    val videoUrl: String,

    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String
)