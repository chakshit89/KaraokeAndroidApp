package com.karoke.myoyasing.domain.model

data class FeedItem(
    val id: String,
    val videoUrl: String?,
    val audioUrl: String?,
    val score: Int,
    val grade: String,
    val pitchAccuracy: Double,
    val timingAccuracy: Double,
    val description: String?,
    val createdAt: String,
    val thumbnailUrl: String?,
    val likes: Int,
    val shareCount: Int,
    val commentCount: Int,
    val isLiked: Boolean,
    val user: FeedUser,
    val song: FeedSong
)

data class FeedUser(
    val id: String,
    val name: String,
    val profileImageUrl: String?
)

data class FeedSong(
    val id: String,
    val title: String,
    val artist: FeedArtist,
    val coverImageUrl: String?
)

data class FeedArtist(
    val id: String,
    val name: String,
    val isVerified: Boolean,
    val profileImageUrl: String?
)