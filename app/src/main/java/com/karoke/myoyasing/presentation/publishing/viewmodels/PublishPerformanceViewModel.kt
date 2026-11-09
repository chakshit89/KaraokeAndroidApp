package com.karoke.myoyasing.presentation.publishing.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.domain.model.PublishPerformanceRequest
import com.karoke.myoyasing.domain.usecase.PublishPerformanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class PublishPerformanceViewModel(
    private val publishPerformanceUseCase: PublishPerformanceUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(PublishPerformanceUiState())

    val uiState = _uiState.asStateFlow()

    fun publishPerformance(
        performanceId: Int,
        description: String,
        audience: String = "PUBLIC",
        collaboratorIds: List<Int> = emptyList(),
        hashtagIds: List<Int> = emptyList()
    ) {
        if (_uiState.value.isLoading) return

        if (performanceId <= 0) {
            _uiState.update {
                it.copy(
                    isPublished = false,
                    error = "Invalid performance ID.",
                    message = null
                )
            }
            return
        }

        if (description.isBlank()) {
            _uiState.update {
                it.copy(
                    isPublished = false,
                    error = "Please enter a description.",
                    message = null
                )
            }
            return
        }

        val request = PublishPerformanceRequest(
            description = description.trim(),
            audience = audience,
            collaboratorIds = collaboratorIds,
            hashtagIds = hashtagIds
        )

        viewModelScope.launch {
            _uiState.value = PublishPerformanceUiState(
                isLoading = true
            )

            try {
                val result = publishPerformanceUseCase(
                    performanceId = performanceId,
                    request = request
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = PublishPerformanceUiState(
                            isLoading = false,
                            isPublished = true,
                            message = message
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = PublishPerformanceUiState(
                            isLoading = false,
                            error = exception.message
                                ?: "Unable to publish performance."
                        )
                    }
                )

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {
                _uiState.value = PublishPerformanceUiState(
                    isLoading = false,
                    error = exception.message
                        ?: "An unexpected error occurred."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(message = null)
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}