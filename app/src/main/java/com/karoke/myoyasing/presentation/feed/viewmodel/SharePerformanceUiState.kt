package com.karoke.myoyasing.presentation.feed.viewmodel

import com.karoke.myoyasing.domain.model.SharePerformance

data class SharePerformanceUiState(
    val isLoading: Boolean = false,
    val sharePerformance: SharePerformance? = null,
    val error: String? = null
)