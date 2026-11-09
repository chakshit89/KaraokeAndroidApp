package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.model.CreatePerformanceResult

interface CreatePerformanceRepo {
    suspend fun createPerformance(
        songid: Int,
        audioUrl: String,
        videoUrl: String?,
        score: Int,
        grade: String,
        pitchaccuracy: Int,
        timingaccuracy: Int
    ): ApiResult<CreatePerformanceResult>
}