package com.karoke.myoyasing.data.dto.FeedDTO

import com.google.gson.annotations.SerializedName

data class FeedResponseDto(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: List<FeedItemDto> = emptyList()
)

data class FeedItemDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("videoUrl")
    val videoUrl: String? = null,

    @SerializedName("audioUrl")
    val audioUrl: String? = null,

    @SerializedName("songId")
    val songId: String = "",

    @SerializedName("songTitle")
    val songTitle: String = "",

    // The API currently returns artist: null.
    @SerializedName("artist")
    val artist: FeedArtistDto? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("createdAt")
    val createdAt: String = "",

    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String? = null,

    @SerializedName("score")
    val score: Int = 0,

    @SerializedName("grade")
    val grade: String = "",

    @SerializedName("pitchAccuracy")
    val pitchAccuracy: Double = 0.0,

    @SerializedName("timingAccuracy")
    val timingAccuracy: Double = 0.0,

    @SerializedName("likes")
    val likes: Int = 0,

    @SerializedName("shareCount")
    val shareCount: Int = 0,

    @SerializedName("commentCount")
    val commentCount: Int=0,

    @SerializedName("isLiked")
    val isLiked: Boolean = false,

    @SerializedName("user")
    val user: FeedUserDto? = null
)

data class FeedUserDto(
    @SerializedName("id")
    val id: String = "",

    // Nullable because the API returns name: null.
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null
)

data class FeedArtistDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("isVerified")
    val isVerified: Boolean = false,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null
)