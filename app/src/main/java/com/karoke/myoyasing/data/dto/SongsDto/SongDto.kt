package com.karoke.myoyasing.data.dto.SongsDto

import com.google.gson.annotations.SerializedName

data class SongDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("artist")
    val artist: String?,

    @SerializedName("coverImageUrl")
    val coverImageUrl: String,

    @SerializedName("audioUrl")
    val audioUrl: String,

    @SerializedName("duration")
    val duration: Int,

    @SerializedName("releaseDate")
    val releaseDate: String?,

    @SerializedName("likes")
    val likes: Int,

    @SerializedName("isLiked")
    val isLiked: Boolean,

    @SerializedName("assetCode")
    val assetCode: String,

    @SerializedName("instrumentalAudioUrl")
    val instrumentalAudioUrl: String,

    @SerializedName("lyricsUrl")
    val lyricsUrl: String
)