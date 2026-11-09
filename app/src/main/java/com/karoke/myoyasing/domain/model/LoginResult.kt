package com.karoke.myoyasing.domain.model

data class LoginResult(
    val message: String?,
    val userId: Int,
    val accessToken: String?,
    val phoneVerified: Boolean
)