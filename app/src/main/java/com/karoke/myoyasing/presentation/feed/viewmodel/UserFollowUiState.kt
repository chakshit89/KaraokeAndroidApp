package com.karoke.myoyasing.presentation.feed.viewmodel

data class UserFollowUiState(
    val isFollowing: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)