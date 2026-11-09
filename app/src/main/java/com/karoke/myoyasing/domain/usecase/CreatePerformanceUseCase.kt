package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.model.CreatePerformanceResult
import com.karoke.myoyasing.domain.repo.CreatePerformanceRepo

class CreatePerformanceUseCase(
    private val repository: CreatePerformanceRepo
) {
    suspend operator fun invoke(
        songId: Int,
        audioUrl: String,
        videoUrl: String?,
        score: Int,
        grade: String,
        pitchAccuracy: Int,
        timingAccuracy: Int
    ): ApiResult<CreatePerformanceResult> {

        return repository.createPerformance(
            songid = songId,
            audioUrl = audioUrl,
            videoUrl = videoUrl,
            score = score,
            grade = grade,
            pitchaccuracy = pitchAccuracy,
            timingaccuracy = timingAccuracy
        )
    }
}