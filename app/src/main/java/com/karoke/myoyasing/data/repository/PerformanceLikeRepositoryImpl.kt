package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.remote.LoginApiService

import com.karoke.myoyasing.domain.repo.PerformanceLikeRepository

class PerformanceLikeRepositoryImpl(
    private val api: LoginApiService
) : PerformanceLikeRepository {

    override suspend fun likePerformance(
        performanceId: Long
    ): Result<Unit> {

        return try {
            val response = api.likePerformance(performanceId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string()
                            ?: "Failed to like performance"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikePerformance(
        performanceId: Long
    ): Result<Unit> {

        return try {
            val response = api.unlikePerformance(performanceId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string()
                            ?: "Failed to unlike performance"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}