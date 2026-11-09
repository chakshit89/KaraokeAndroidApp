package com.karoke.myoyasing.presentation.navigation.routes

sealed class AppDestinationRoutes(
    val route: String
) {

    data object Login : AppDestinationRoutes("login")
    data object Splash : AppDestinationRoutes(
        route = "splash"
    )

    data object OtpVerification : AppDestinationRoutes(
        "otp_verification/{phoneNumber}"
    ) {
        const val baseRoute = "otp_verification"
        const val phoneNumberArgument = "phoneNumber"
    }
}