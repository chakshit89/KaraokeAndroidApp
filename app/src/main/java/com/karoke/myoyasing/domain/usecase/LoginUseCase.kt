package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.model.LoginResult
import com.karoke.myoyasing.domain.repo.LoginRepository

class LoginUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(
        countryCode: String,
        phoneNumber: String
    ): ApiResult<LoginResult> {

        if (countryCode.isBlank()) {
            return ApiResult.Error(
                message = "Please select a country."
            )
        }

        if (!countryCode.startsWith("+")) {
            return ApiResult.Error(
                message = "Invalid country calling code."
            )
        }

        if (phoneNumber.isBlank()) {
            return ApiResult.Error(
                message = "Please enter your phone number."
            )
        }

        return repository.login(
            countryCode = countryCode,
            phoneNumber = phoneNumber
        )
    }
}