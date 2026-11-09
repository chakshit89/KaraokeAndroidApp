package com.karoke.myoyasing.presentation.feed.viewmodel

import com.karoke.myoyasing.domain.model.FeedItem

data class FeedUiState(
    val isLoading: Boolean = false,
    val feedItems: List<FeedItem> = emptyList(),
    val errorMessage: String? = null
)