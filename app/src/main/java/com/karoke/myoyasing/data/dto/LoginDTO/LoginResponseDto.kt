package com.karoke.myoyasing.data.dto.LoginDTO

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("userId")
    val userId: Int? = null,

    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("phoneVerified")
    val phoneVerified: Boolean? = null
)