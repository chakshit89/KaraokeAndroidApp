package com.karoke.myoyasing.presentation.featureUI.auth.login

import com.karoke.myoyasing.domain.model.LoginResult

data class LoginUiState(
    val isLoading: Boolean = false,
    val result: LoginResult? = null,
    val error: String? = null
)