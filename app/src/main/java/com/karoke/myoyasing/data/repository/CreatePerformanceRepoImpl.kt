package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.dto.CreatePerformanceDTO.CreatePerformanceRequestDTO
import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.model.CreatePerformanceResult
import com.karoke.myoyasing.domain.repo.CreatePerformanceRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreatePerformanceRepoImpl(
    private val apiService: LoginApiService
) : CreatePerformanceRepo {

    override suspend fun createPerformance(
        songid: Int,
        audioUrl: String,
        videoUrl: String?,
        score: Int,
        grade: String,
        pitchaccuracy: Int,
        timingaccuracy: Int
    ): ApiResult<CreatePerformanceResult> = withContext(Dispatchers.IO) {

        try {
            val request = CreatePerformanceRequestDTO(
                songId = songid,
                audioUrl = audioUrl,
                videoUrl = videoUrl,
                score = score,
                grade = grade,
                pitchAccuracy = pitchaccuracy,
                timingAccuracy = timingaccuracy
            )

            val response = apiService.createPerformance(request)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    ApiResult.Success(
                        CreatePerformanceResult(id = body.id, thumbnailUrl = body.thumbnailUrl)
                    )
                } else {
                    ApiResult.Error("Empty performance response")
                }
            } else {
                ApiResult.Error(
                    "Failed to publish performance: HTTP ${response.code()} " +
                        (response.errorBody()?.string() ?: "")
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(
                e.message ?: "Unable to publish performance"
            )
        }
    }
}