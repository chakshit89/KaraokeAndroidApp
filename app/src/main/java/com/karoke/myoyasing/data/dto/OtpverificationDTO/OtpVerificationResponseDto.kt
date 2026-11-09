package com.karoke.myoyasing.data.dto.OtpverificationDTO

data class OtpVerificationResponseDto(
    val message: String?,
    val userId: Int?,
    val accessToken: String?,
    val phoneVerified: Boolean?
)