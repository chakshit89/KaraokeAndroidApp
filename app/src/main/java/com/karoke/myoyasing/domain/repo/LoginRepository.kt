package com.karoke.myoyasing.domain.repo


import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.model.LoginResult

interface LoginRepository {

    suspend fun login(
        countryCode: String,
        phoneNumber: String
    ): ApiResult<LoginResult>
}