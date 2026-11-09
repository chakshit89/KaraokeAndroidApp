package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.domain.model.MelodyComparisonResult
import java.io.File

interface MelodyRepository {

    suspend fun compareMelody(
        songId: Long,
        userMelodyFile: File
    ): Result<MelodyComparisonResult>
}