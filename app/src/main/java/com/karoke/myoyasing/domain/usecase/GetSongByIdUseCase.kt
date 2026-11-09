package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.repo.SongsRepository

class GetSongByIdUseCase(
    private val repository: SongsRepository
) {
    suspend operator fun invoke(songId: String) =
        repository.getSongById(songId)
}