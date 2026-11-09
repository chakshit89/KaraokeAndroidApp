package com.karoke.myoyasing.data.repository

import android.util.Log
import com.karoke.myoyasing.data.dto.publishPerformanceDTO.PublishPerformanceRequestDto
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.model.PublishPerformanceRequest
import com.karoke.myoyasing.domain.repo.PerformanceRepository
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class PerformanceRepositoryImpl(
    private val apiService: LoginApiService
) : PerformanceRepository {

    override suspend fun publishPerformance(
        performanceId: Int,
        request: PublishPerformanceRequest
    ): Result<String> {

        return try {
            require(performanceId > 0) {
                "Performance ID must be greater than zero."
            }

            val requestDto = PublishPerformanceRequestDto(
                description = request.description,
                audience = request.audience,
                collaboratorIds = request.collaboratorIds,
                hashtagIds = request.hashtagIds
            )

            val response = apiService.publishPerformance(
                performanceId = performanceId,
                request = requestDto
            )

            if (response.isSuccessful) {
                val message = response.body()
                    ?.use { it.string() }
                    ?.takeIf { it.isNotBlank() }
                    ?: "Performance published successfully."

                Result.success(message)
            } else {
                val errorMessage = response.errorBody()
                    ?.use { it.string() }
                    ?.takeIf { it.isNotBlank() }
                    ?: "Failed to publish performance. HTTP ${response.code()}."

                Result.failure(
                    IOException(errorMessage)
                )
            }

        } catch (exception: CancellationException) {
            // Preserve coroutine cancellation.
            throw exception

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}