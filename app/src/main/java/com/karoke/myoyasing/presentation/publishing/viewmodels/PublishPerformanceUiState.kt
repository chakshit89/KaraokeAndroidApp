package com.karoke.myoyasing.presentation.publishing.viewmodels

data class PublishPerformanceUiState(
    val isLoading: Boolean = false,
    val isPublished: Boolean = false,
    val message: String? = null,
    val error: String? = null
)