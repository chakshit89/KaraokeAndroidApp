package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.model.PublishPerformanceRequest
import com.karoke.myoyasing.domain.repo.PerformanceRepository

class PublishPerformanceUseCase(
    private val repository: PerformanceRepository
) {
    suspend operator fun invoke(
        performanceId: Int,
        request: PublishPerformanceRequest
    ): Result<String> {

        return repository.publishPerformance(
            performanceId = performanceId,
            request = request
        )
    }
}