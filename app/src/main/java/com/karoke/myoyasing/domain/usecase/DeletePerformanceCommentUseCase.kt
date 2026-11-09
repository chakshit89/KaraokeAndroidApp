package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.repo.PerformanceCommentsRepository


class DeletePerformanceCommentUseCase(
    private val repository: PerformanceCommentsRepository
) {

    suspend operator fun invoke(
        performanceId: Long,
        commentId: Long
    ): Result<Unit> {

        return repository.deletePerformanceComment(
            performanceId = performanceId,
            commentId = commentId
        )
    }
}