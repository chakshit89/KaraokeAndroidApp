package com.karoke.myoyasing.presentation.featureUI.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.data.local.AppDataStore
import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.usecase.LoginUseCase
import com.karoke.myoyasing.domain.usecase.VerifyOtpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class OtpVerificationViewModel(
    private val verifyOtpUseCase: VerifyOtpUseCase, private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpVerificationUiState())
    val uiState = _uiState.asStateFlow()

    fun verifyOtp(
        countryCode: String,
        phoneNumber: String,
        otp: String
    ) {
        if (_uiState.value.isLoading) return

        if (otp.length != 4 || !otp.all(Char::isDigit)) {
            _uiState.update {
                it.copy(error = "Please enter the complete 4-digit OTP.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    result = null,
                    error = null
                )
            }

            try {
                when (
                    val response = verifyOtpUseCase(
                        countryCode = countryCode,
                        phoneNumber = phoneNumber,
                        otp = otp
                    )
                ) {
                    is ApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                result = response.data,
                                error = null
                            )
                        }
                        AppDataStore.saveIsLogin(true)
                    }

                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                result = null,
                                error = response.message
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        result = null,
                        error = "Unable to verify OTP. Please try again."
                    )
                }
            }
        }
    }

    fun resendOTP(
        countryCode: String,
        phoneNumber: String
    ) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                when (
                    val response = loginUseCase(
                        countryCode,
                        phoneNumber
                    )
                ) {
                    is ApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.message
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Unable to resend OTP. Please try again."
                    )
                }
            }
        }
    }

    fun clearOTPResult() {
        _uiState.update {
            it.copy(result = null)
        }
    }
}