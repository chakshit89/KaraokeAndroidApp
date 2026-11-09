package com.karoke.myoyasing.data.mapper

import com.karoke.myoyasing.data.dto.SongsDto.SongDto
import com.karoke.myoyasing.di.network.MainRetrofitClient
import com.karoke.myoyasing.domain.model.Song

private fun String.toAbsoluteUrl(): String {
    if (
        startsWith("https://", ignoreCase = true) ||
        startsWith("http://", ignoreCase = true)
    ) {
        return this
    }

    return MainRetrofitClient.BASE_URL.trimEnd('/') +
        "/" + trimStart('/')
}

fun SongDto.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artist = artist,
        coverImageUrl = coverImageUrl.toAbsoluteUrl(),
        audioUrl = audioUrl.toAbsoluteUrl(),
        duration = duration,
        releaseDate = releaseDate,
        likes = likes,
        isLiked = isLiked,
        assetCode = assetCode,
        instrumentalAudioUrl =
            instrumentalAudioUrl.toAbsoluteUrl(),
        lyricsUrl = lyricsUrl.toAbsoluteUrl()
    )
}