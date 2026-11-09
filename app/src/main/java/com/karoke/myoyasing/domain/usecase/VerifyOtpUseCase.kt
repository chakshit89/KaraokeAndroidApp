package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.model.OtpVerificationResult
import com.karoke.myoyasing.domain.repo.OtpVerificationRepository

class VerifyOtpUseCase(
    private val repository: OtpVerificationRepository
) {
    suspend operator fun invoke(
        countryCode: String,
        phoneNumber: String,
        otp: String
    ): ApiResult<OtpVerificationResult> {
        return repository.verifyOtp(
            countryCode = countryCode,
            phoneNumber = phoneNumber,
            otp = otp
        )
    }
}