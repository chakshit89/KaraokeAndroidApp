package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.model.SharePerformance
import com.karoke.myoyasing.domain.repo.SharePerformanceRepository

class SharePerformanceRepositoryImpl(
    private val api: LoginApiService
) : SharePerformanceRepository {

    override suspend fun sharePerformance(
        performanceId: Long
    ): Result<SharePerformance> {

        return try {

            val response = api.sharePerformance(performanceId)

            if (response.isSuccessful) {

                val body = response.body()
                    ?: return Result.failure(
                        Exception("Empty response from server")
                    )

                Result.success(
                    SharePerformance(
                        performanceId = body.performanceId,
                        shareUrl = body.shareUrl,
                        mediaUrl = body.mediaUrl,
                        message = body.message
                    )
                )

            } else {

                Result.failure(
                    Exception(
                        response.errorBody()?.string()
                            ?: "Failed to share performance"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}