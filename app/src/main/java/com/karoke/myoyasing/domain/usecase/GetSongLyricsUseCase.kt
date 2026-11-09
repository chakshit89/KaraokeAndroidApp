package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.repo.SongsRepository

class GetSongLyricsUseCase(
    private val repository: SongsRepository
) {
    suspend operator fun invoke(lyricsUrl: String) =
        repository.getLyrics(lyricsUrl)
}