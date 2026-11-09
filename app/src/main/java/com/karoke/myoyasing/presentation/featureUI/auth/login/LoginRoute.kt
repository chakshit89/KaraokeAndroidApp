package com.karoke.myoyasing.presentation.featureUI.auth.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.karoke.myoyasing.core.utils.CommonLoading
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoute(
    onNavigateToOtp: (
        countryCode: String,
        phoneNumber: String
    ) -> Unit,
    onClose: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var pendingCountryCode by remember {
        mutableStateOf("")
    }

    var pendingPhoneNumber by remember {
        mutableStateOf("")
    }



    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {

            viewModel.clearLoginResult()

            onNavigateToOtp(
                pendingCountryCode,
                pendingPhoneNumber
            )
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {

        LoginScreen(
            isLoading = uiState.isLoading,
            error = uiState.error,
            onClose = onClose,
            onContinue = { countryCode, phoneNumber ->
                pendingCountryCode = countryCode
                pendingPhoneNumber = phoneNumber

                viewModel.login(
                    countryCode = countryCode,
                    phoneNumber = phoneNumber
                )
            }
        )

        CommonLoading(
            isLoading = uiState.isLoading,
            message = "Sending OTP..."
        )
    }
}