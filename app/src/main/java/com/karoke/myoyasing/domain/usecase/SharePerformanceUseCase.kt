package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.model.SharePerformance
import com.karoke.myoyasing.domain.repo.SharePerformanceRepository

class SharePerformanceUseCase(
    private val repository: SharePerformanceRepository
) {

    suspend operator fun invoke(
        performanceId: Long
    ): Result<SharePerformance> {

        return repository.sharePerformance(performanceId)
    }
}