package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.model.PerformanceComment
import com.karoke.myoyasing.domain.repo.PerformanceCommentsRepository

class AddPerformanceCommentUseCase(
    private val repository: PerformanceCommentsRepository
) {

    suspend operator fun invoke(
        performanceId: Long,
        content: String
    ): Result<PerformanceComment> {

        if (content.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Comment cannot be empty")
            )
        }

        return repository.addPerformanceComment(
            performanceId = performanceId,
            content = content.trim()
        )
    }
}