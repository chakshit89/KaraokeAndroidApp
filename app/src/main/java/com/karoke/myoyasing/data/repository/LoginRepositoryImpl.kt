package com.karoke.myoyasing.data.repository

import android.util.Log
import com.karoke.myoyasing.data.dto.LoginDTO.LoginRequestDto
import com.karoke.myoyasing.data.local.AppDataStore
import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.data.remote.SafeApiCall
import com.karoke.myoyasing.domain.model.LoginResult
import com.karoke.myoyasing.domain.repo.LoginRepository

class LoginRepositoryImpl(
    private val apiService: LoginApiService
) : LoginRepository {


    override suspend fun login(
        countryCode: String,
        phoneNumber: String
    ): ApiResult<LoginResult> {

        val request = LoginRequestDto(
            countryCode = countryCode.trim(),
            phoneNumber = phoneNumber.trim()
        )

        return when (val result = SafeApiCall.execute {
            apiService.login(request)
        }) {
            is ApiResult.Error -> result

            is ApiResult.Success -> {
                val response = result.data

                Log.d("LoginRepository", "userId = ${response.userId}")
                Log.d("LoginRepository", "phoneVerified = ${response.phoneVerified}")

                val userId = response.userId

                if (userId == null || userId <= 0) {
                    ApiResult.Error(
                        message = "The server returned incomplete login data."
                    )
                } else {
                    ApiResult.Success(
                        LoginResult(
                            message = response.message,
                            userId = userId,
                            accessToken = response.accessToken,
                            phoneVerified = response.phoneVerified == true
                        )
                    )
                }
            }
        }
    }
}
