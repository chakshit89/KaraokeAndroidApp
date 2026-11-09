package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.model.PerformanceComment
import com.karoke.myoyasing.domain.repo.PerformanceCommentsRepository

class GetPerformanceCommentsUseCase(
    private val repository: PerformanceCommentsRepository
) {

    suspend operator fun invoke(
        performanceId: Long,
        page: Int = 0,
        size: Int = 20,
        sort: List<String>? = null
    ): Result<List<PerformanceComment>> {

        return repository.getPerformanceComments(
            performanceId = performanceId,
            page = page,
            size = size,
            sort = sort
        )
    }
}