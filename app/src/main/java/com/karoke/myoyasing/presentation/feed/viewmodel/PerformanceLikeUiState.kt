package com.karoke.myoyasing.presentation.feed.viewmodel

data class PerformanceLikeUiState(
    val isLiked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)