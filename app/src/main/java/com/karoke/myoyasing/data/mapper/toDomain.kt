package com.karoke.myoyasing.data.mapper

import com.karoke.myoyasing.core.utils.toAbsoluteUrl
import com.karoke.myoyasing.data.dto.FeedDTO.FeedItemDto
import com.karoke.myoyasing.domain.model.FeedArtist
import com.karoke.myoyasing.domain.model.FeedItem
import com.karoke.myoyasing.domain.model.FeedSong
import com.karoke.myoyasing.domain.model.FeedUser

fun FeedItemDto.toDomain(): FeedItem {
    val artistName = artist?.name.orEmpty()

    return FeedItem(
        id = id,
        videoUrl = videoUrl.toAbsoluteUrl(),
        audioUrl = audioUrl.toAbsoluteUrl(),
        score = score,
        grade = grade,
        pitchAccuracy = pitchAccuracy,
        timingAccuracy = timingAccuracy,
        description = description,
        createdAt = createdAt,
        thumbnailUrl = thumbnailUrl.toAbsoluteUrl(),
        likes = likes,
        shareCount = shareCount,
        commentCount=commentCount,
        isLiked = isLiked,

        user = FeedUser(
            id = user?.id.orEmpty(),
            name = user?.name.orEmpty().ifBlank { "UnknownSinger" },
            profileImageUrl = user?.profileImageUrl.toAbsoluteUrl()
        ),

        song = FeedSong(
            id = songId,
            title = songTitle,
            coverImageUrl = thumbnailUrl.toAbsoluteUrl(),
            artist = FeedArtist(
                id = artist?.id.orEmpty(),
                name = artistName,
                isVerified = artist?.isVerified ?: false,
                profileImageUrl = artist?.profileImageUrl.toAbsoluteUrl()
            )
        )
    )
}