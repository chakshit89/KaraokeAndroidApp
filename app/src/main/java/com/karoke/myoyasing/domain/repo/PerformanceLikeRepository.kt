package com.karoke.myoyasing.domain.repo

interface PerformanceLikeRepository {

    suspend fun likePerformance(
        performanceId: Long
    ): Result<Unit>

    suspend fun unlikePerformance(
        performanceId: Long
    ): Result<Unit>
}