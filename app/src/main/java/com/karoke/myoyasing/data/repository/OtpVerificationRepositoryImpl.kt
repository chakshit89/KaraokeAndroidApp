package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.dto.OtpverificationDTO.OtpVerificationRequestDto
import com.karoke.myoyasing.data.dto.OtpverificationDTO.OtpVerificationResponseDto
import com.karoke.myoyasing.data.local.AppDataStore
import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.data.remote.SafeApiCall
import com.karoke.myoyasing.domain.model.OtpVerificationResult
import com.karoke.myoyasing.domain.repo.OtpVerificationRepository

class OtpVerificationRepositoryImpl(
    private val apiService: LoginApiService
) : OtpVerificationRepository {

    override suspend fun verifyOtp(
        countryCode: String,
        phoneNumber: String,
        otp: String
    ): ApiResult<OtpVerificationResult> {

        val request = OtpVerificationRequestDto(
            countryCode = countryCode.trim(),
            phoneNumber = phoneNumber.trim(),
            otp = otp.trim()
        )

        return when (
            val result = SafeApiCall.execute<OtpVerificationResponseDto> {
                apiService.verifyOtp(request)
            }
        ) {
            is ApiResult.Error -> result

            is ApiResult.Success -> {
                val response = result.data
                val userId = response.userId
                val accessToken = response.accessToken

                when {
                    userId == null || userId <= 0 -> {
                        ApiResult.Error(
                            message = "Invalid user information returned by server."
                        )
                    }

                    accessToken.isNullOrBlank() -> {
                        ApiResult.Error(
                            message = "The server did not return an access token."
                        )
                    }

//                    response.phoneVerified != true -> {
//                        ApiResult.Error(
//                            message = "Phone number verification was not completed."
//                        )
//                    }

                    else -> {
                        try {
                            AppDataStore.saveAccessToken(accessToken)
                            AppDataStore.saveUserid(userId.toString())

                            ApiResult.Success(
                                OtpVerificationResult(
                                    message = response.message
                                        ?: "Login successful",
                                    userId = userId,
                                    accessToken = accessToken,
                                    phoneVerified = true
                                )
                            )
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (_: Exception) {
                            ApiResult.Error(
                                message = "Unable to save your login session."
                            )
                        }
                    }
                }
            }
        }
    }
}