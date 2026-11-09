package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.domain.model.OtpVerificationResult
import com.karoke.myoyasing.data.remote.ApiResult

interface OtpVerificationRepository {

    suspend fun verifyOtp(
        countryCode: String,
        phoneNumber: String,
        otp: String
    ): ApiResult<OtpVerificationResult>
}