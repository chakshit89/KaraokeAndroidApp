package com.karoke.myoyasing.domain.model

data class UploadedMedia(
    val audioUrl: String,
    val videoUrl: String?,
    val thumbnailUrl: String?
)