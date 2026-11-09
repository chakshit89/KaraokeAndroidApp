package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.repo.PerformanceLikeRepository

class UnlikePerformanceUseCase(
    private val repository: PerformanceLikeRepository
) {

    suspend operator fun invoke(
        performanceId: Long
    ): Result<Unit> {
        return repository.unlikePerformance(performanceId)
    }
}