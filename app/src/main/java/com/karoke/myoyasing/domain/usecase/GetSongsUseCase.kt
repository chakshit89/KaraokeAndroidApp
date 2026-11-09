package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.repo.SongsRepository

class GetSongsUseCase(
    private val repository: SongsRepository
) {
    suspend operator fun invoke() =
        repository.getSongs()
}