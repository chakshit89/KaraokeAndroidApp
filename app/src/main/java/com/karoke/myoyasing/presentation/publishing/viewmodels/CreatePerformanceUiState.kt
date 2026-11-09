package com.karoke.myoyasing.presentation.publishing.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.usecase.CreatePerformanceUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreatePerformanceUiState(
    val isPublishing: Boolean = false,
    val performanceId: Int? = null,
    val isPublished: Boolean = false,
    val errorMessage: String? = null
)

class CreatePerformanceViewModel(
    private val createPerformanceUseCase: CreatePerformanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CreatePerformanceUiState()
    )

    val uiState: StateFlow<CreatePerformanceUiState> =
        _uiState.asStateFlow()

    fun publishPerformance(
        songId: Int,
        audioUrl: String,
        videoUrl: String?,
        score: Int,
        grade: String,
        pitchAccuracy: Int,
        timingAccuracy: Int
    ) {
        if (_uiState.value.isPublishing) return

        // Audio URL is required. Video URL is optional.
        if (audioUrl.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Uploaded audio URL is missing."
            )
            return
        }

        _uiState.value = CreatePerformanceUiState(
            isPublishing = true
        )

        viewModelScope.launch {
            try {
                Log.d(
                    "CreatePerformanceVM",
                    "Calling create performance API"
                )
                Log.d(
                    "CreatePerformanceVM",
                    "songId=$songId, audioUrl=$audioUrl, videoUrl=$videoUrl"
                )

                when (
                    val result = createPerformanceUseCase(
                        songId = songId,
                        audioUrl = audioUrl,
                        videoUrl = videoUrl,
                        score = score,
                        grade = grade,
                        pitchAccuracy = pitchAccuracy,
                        timingAccuracy = timingAccuracy
                    )
                ) {
                    is ApiResult.Success -> {
                        val data = result.data

                        _uiState.value = CreatePerformanceUiState(
                            isPublishing = false,
                            performanceId = data.id,
                            isPublished = true
                        )

                        Log.d(
                            "CreatePerformanceVM",
                            "Create performance succeeded. ID=${data.id}"
                        )
                    }

                    is ApiResult.Error -> {
                        Log.e(
                            "CreatePerformanceVM",
                            "Create performance failed: ${result.message}"
                        )

                        _uiState.value = CreatePerformanceUiState(
                            isPublishing = false,
                            errorMessage = result.message
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(
                    "CreatePerformanceVM",
                    "Exception while creating performance",
                    e
                )

                _uiState.value = CreatePerformanceUiState(
                    isPublishing = false,
                    errorMessage = e.message
                        ?: "Unable to create performance."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}