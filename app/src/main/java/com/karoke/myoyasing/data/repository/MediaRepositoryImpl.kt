package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.dto.uploadsongsDTO.UploadMediaResponseDto
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.model.UploadedMedia
import com.karoke.myoyasing.domain.repo.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MediaRepositoryImpl(
    private val apiService: LoginApiService
) : MediaRepository {

    override suspend fun uploadMedia(
        audioFile: File,
        thumbnailUrl: String
    ): Result<UploadedMedia> = withContext(Dispatchers.IO) {
        runCatching {
            require(audioFile.exists() && audioFile.length() > 0) {
                "Recording file does not exist or is empty."
            }

            val thumbnailFile = File.createTempFile(
                "karaoke_thumbnail_",
                ".jpg"
            )

            try {
                val request = okhttp3.Request.Builder()
                    .url(thumbnailUrl)
                    .get()
                    .build()

                val client = okhttp3.OkHttpClient()

                client.newCall(request).execute().use { response ->
                    check(response.isSuccessful) {
                        "Thumbnail download failed: HTTP ${response.code}"
                    }

                    val body = requireNotNull(response.body) {
                        "Thumbnail response is empty."
                    }

                    thumbnailFile.outputStream().use { output ->
                        body.byteStream().copyTo(output)
                    }
                }

                require(thumbnailFile.length() > 0) {
                    "Downloaded thumbnail is empty."
                }

                val audioPart = MultipartBody.Part.createFormData(
                    "audio",
                    audioFile.name,
                    audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
                )

                val emptyBody = ByteArray(0).toRequestBody(null)

                val videoPart = MultipartBody.Part.createFormData(
                    "video",
                    "",
                    emptyBody
                )

                val thumbnailPart = MultipartBody.Part.createFormData(
                    "thumbnail",
                    thumbnailFile.name,
                    thumbnailFile.asRequestBody(
                        "image/jpeg".toMediaTypeOrNull()
                    )
                )

                val response = apiService.uploadMedia(
                    audio = audioPart,
                    video = videoPart,
                    thumbnail = thumbnailPart
                )

                check(response.isSuccessful) {
                    "Media upload failed: HTTP ${response.code()} " +
                            response.errorBody()?.string().orEmpty()
                }

                val result = requireNotNull(response.body()) {
                    "Upload response body is empty."
                }

                UploadedMedia(
                    audioUrl = result.audioUrl,
                    videoUrl = result.videoUrl,
                    thumbnailUrl = result.thumbnailUrl
                )
            } finally {
                thumbnailFile.delete()
            }
        }
    }
}