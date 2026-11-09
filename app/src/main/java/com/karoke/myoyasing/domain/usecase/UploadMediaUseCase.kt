package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.model.UploadedMedia
import com.karoke.myoyasing.domain.repo.MediaRepository
import java.io.File

class UploadMediaUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(
        audioFile: File,
        thumbnailUrl: String
    ): Result<UploadedMedia> {
        return repository.uploadMedia(
            audioFile = audioFile,
            thumbnailUrl = thumbnailUrl
        )
    }
}