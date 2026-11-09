package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.domain.model.PublishPerformanceRequest

interface PerformanceRepository {

    suspend fun publishPerformance(
        performanceId: Int,
        request: PublishPerformanceRequest
    ): Result<String>
}