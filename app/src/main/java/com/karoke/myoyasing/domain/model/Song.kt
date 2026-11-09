package com.karoke.myoyasing.domain.model

data class Song(
    val id: String,
    val title: String,
    val artist: String?,
    val coverImageUrl: String,
    val audioUrl: String,
    val duration: Int,
    val releaseDate: String?,
    val likes: Int,
    val isLiked: Boolean,
    val assetCode: String,
    val instrumentalAudioUrl: String,
    val lyricsUrl: String
)