package com.karoke.myoyasing.presentation.featureUI.auth.otp

import com.karoke.myoyasing.domain.model.OtpVerificationResult

data class OtpVerificationUiState(
    val isLoading: Boolean = false,
    val result: OtpVerificationResult? = null,
    val error: String? = null
)