package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.model.MelodyComparisonResult
import com.karoke.myoyasing.domain.repo.MelodyRepository
import java.io.File

class CompareMelodyUseCase(
    private val repository: MelodyRepository
) {

    suspend operator fun invoke(
        songId: Long,
        userMelodyFile: File
    ): Result<MelodyComparisonResult> {

        return repository.compareMelody(
            songId = songId,
            userMelodyFile = userMelodyFile
        )
    }
}