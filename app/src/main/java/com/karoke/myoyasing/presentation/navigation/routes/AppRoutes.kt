
package com.karoke.myoyasing.presentation.navigation.routes

import android.net.Uri


object AppRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val OTP = "otp?countryCode={countryCode}&phoneNumber={phoneNumber}"

    fun otp(countryCode: String, phoneNumber: String): String {
        return "otp?countryCode=${Uri.encode(countryCode)}" +
                "&phoneNumber=${Uri.encode(phoneNumber)}"
    }

    const val MAIN = "main"
    const val KARAOKE_PLAYER = "karaoke_player"
    const val KARAOKE_PERMISSIONS = "karaoke_permissions/{songId}"
    const val PERFORMANCE = "performance"
    const val PUBLISHPERFORMANCE = "publishperformance"

    const val HOME = "home"
    const val SING = "sing"
    const val FEED = "feed"
    const val PROFILE = "profile"

    const val RECORDING = "recording/{songId}"

    fun recording(songId: String): String {
        return "recording/${Uri.encode(songId)}"
    }

    fun karaokePermissions(songId: String): String {
        return "karaoke_permissions/${Uri.encode(songId)}"
    }
}
