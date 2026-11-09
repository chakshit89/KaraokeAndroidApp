package com.karoke.myoyasing.presentation.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.domain.usecase.LikePerformanceUseCase
import com.karoke.myoyasing.domain.usecase.UnlikePerformanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerformanceLikeViewModel(
    private val likePerformanceUseCase: LikePerformanceUseCase,
    private val unlikePerformanceUseCase: UnlikePerformanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerformanceLikeUiState())
    val uiState: StateFlow<PerformanceLikeUiState> =
        _uiState.asStateFlow()

    fun toggleLike(
        performanceId: Long,
        currentlyLiked: Boolean
    ) {

        if (_uiState.value.isLoading) return

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = if (currentlyLiked) {
                unlikePerformanceUseCase(performanceId)
            } else {
                likePerformanceUseCase(performanceId)
            }

            result
                .onSuccess {
                    _uiState.value = PerformanceLikeUiState(
                        isLiked = !currentlyLiked,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                            ?: "Something went wrong"
                    )
                }
        }
    }
}