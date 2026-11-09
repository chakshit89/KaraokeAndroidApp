package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.domain.model.SharePerformance

interface SharePerformanceRepository {

    suspend fun sharePerformance(
        performanceId: Long
    ): Result<SharePerformance>
}