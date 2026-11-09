package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.model.MelodyComparisonResult
import com.karoke.myoyasing.domain.repo.MelodyRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class MelodyRepositoryImpl(
    private val apiService: LoginApiService
) : MelodyRepository {

    override suspend fun compareMelody(
        songId: Long,
        userMelodyFile: File
    ): Result<MelodyComparisonResult> {

        return try {

            // Validate the inputs before making the request.
            require(songId > 0L) {
                "Invalid song ID."
            }

            require(userMelodyFile.exists()) {
                "The user melody JSON file does not exist."
            }

            require(userMelodyFile.isFile) {
                "The user melody path must point to a file."
            }

            require(userMelodyFile.length() > 0L) {
                "The user melody JSON file is empty."
            }

            // Convert songId into a multipart form field.
            val songIdBody = songId
                .toString()
                .toRequestBody("text/plain".toMediaType())

            // Convert the JSON file into a file request body.
            val melodyRequestBody = userMelodyFile.asRequestBody(
                "application/json".toMediaType()
            )

            // The multipart field name MUST be userMelody.
            val melodyPart = MultipartBody.Part.createFormData(
                name = "userMelody",
                filename = userMelodyFile.name,
                body = melodyRequestBody
            )

            // Call the backend.
            val response = apiService.compareMelody(
                songId = songIdBody,
                userMelody = melodyPart
            )

            if (response.isSuccessful) {

                val dto = response.body()

                if (dto == null) {
                    Result.failure(
                        Exception(
                            "The server returned an empty response."
                        )
                    )
                } else {

                    Result.success(
                        MelodyComparisonResult(
                            songId = dto.songId,
                            songTitle = dto.songTitle,
                            pitchScore = dto.pitchScore,
                            timingScore = dto.timingScore,
                            completenessScore = dto.completenessScore,
                            finalScore = dto.finalScore,
                            grade = dto.grade,
                            message = dto.message
                        )
                    )
                }

            } else {

                val errorMessage = response.errorBody()
                    ?.string()
                    ?.takeIf { it.isNotBlank() }
                    ?: "Melody comparison failed with HTTP ${response.code()}."

                Result.failure(
                    Exception(errorMessage)
                )
            }

        } catch (e: CancellationException) {

            // Never swallow coroutine cancellation.
            throw e

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}