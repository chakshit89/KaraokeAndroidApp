package com.karoke.myoyasing.presentation.featureUI.auth.otp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.karoke.myoyasing.core.utils.CommonLoading
import org.koin.androidx.compose.koinViewModel

@Composable
fun OtpVerificationRoute(
    countryCode: String,
    phoneNumber: String,
    onVerified: () -> Unit,
    onBack: () -> Unit,
    onEditPhone: () -> Unit,
    onContactSupport: () -> Unit = {},
    viewModel: OtpVerificationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.result) {
        val result = uiState.result

        if (
            result != null &&
            result.userId > 0 &&
            result.phoneVerified
        ) {
            onVerified()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        OtpVerificationScreen(
            countryCode = countryCode,
            phoneNumber = phoneNumber,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onBack = onBack,
            onEditPhone = onEditPhone,
            onContactSupport = onContactSupport,
            onVerify = { otp ->
                viewModel.verifyOtp(
                    countryCode = countryCode,
                    phoneNumber = phoneNumber,
                    otp = otp
                )
            }, onResendOtp = {
                viewModel.resendOTP(countryCode, phoneNumber)

            }
        )
        CommonLoading(
            isLoading = uiState.isLoading,
            message = "Please wait..."
        )

    }
}