package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.domain.model.PerformanceComment

interface PerformanceCommentsRepository {

    suspend fun getPerformanceComments(
        performanceId: Long,
        page: Int = 0,
        size: Int = 20,
        sort: List<String>? = null
    ): Result<List<PerformanceComment>>

    suspend fun addPerformanceComment(
        performanceId: Long,
        content: String
    ): Result<PerformanceComment>

    suspend fun deletePerformanceComment(
        performanceId: Long,
        commentId: Long
    ): Result<Unit>
}