package com.karoke.myoyasing.data.dto.OtpverificationDTO

data class OtpVerificationRequestDto(
    val countryCode: String,
    val phoneNumber: String,
    val otp: String
)