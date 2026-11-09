package com.karoke.myoyasing.presentation.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.domain.usecase.SharePerformanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharePerformanceViewModel(
    private val sharePerformanceUseCase: SharePerformanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharePerformanceUiState())
    val uiState: StateFlow<SharePerformanceUiState> =
        _uiState.asStateFlow()

    fun sharePerformance(performanceId: Long) {

        viewModelScope.launch {

            _uiState.value = SharePerformanceUiState(
                isLoading = true
            )

            sharePerformanceUseCase(performanceId)
                .onSuccess { result ->

                    _uiState.value = SharePerformanceUiState(
                        isLoading = false,
                        sharePerformance = result
                    )
                }
                .onFailure { exception ->

                    _uiState.value = SharePerformanceUiState(
                        isLoading = false,
                        error = exception.message
                            ?: "Something went wrong"
                    )
                }
        }
    }
}