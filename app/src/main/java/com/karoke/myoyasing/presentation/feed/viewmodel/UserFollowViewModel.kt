package com.karoke.myoyasing.presentation.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.domain.usecase.FollowUserUseCase
import com.karoke.myoyasing.domain.usecase.UnfollowUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserFollowViewModel(
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserFollowUiState())
    val uiState: StateFlow<UserFollowUiState> = _uiState.asStateFlow()

    fun followUser(userId: Int) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            followUserUseCase(userId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isFollowing = true,
                        isLoading = false
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.message ?: "Unable to follow user"
                    )
                }
            )
        }
    }

    fun unfollowUser(userId: Int) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            unfollowUserUseCase(userId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isFollowing = false,
                        isLoading = false
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.message ?: "Unable to unfollow user"
                    )
                }
            )
        }
    }
}