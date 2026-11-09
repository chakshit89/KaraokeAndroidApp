package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.domain.model.UploadedMedia
import java.io.File

interface MediaRepository {

    suspend fun uploadMedia(
        audioFile: File,
        thumbnailUrl: String
    ): Result<UploadedMedia>
}