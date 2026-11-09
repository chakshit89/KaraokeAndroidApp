package com.karoke.myoyasing.presentation.navigation.routes

import MainBottomNavScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.karoke.myoyasing.OyaSingSplashScreen
import com.karoke.myoyasing.data.local.AppDataStore
import com.karoke.myoyasing.di.network.SessionExpiredDialog
import com.karoke.myoyasing.di.network.SessionManager
import com.karoke.myoyasing.domain.model.MelodyComparisonResult
import com.karoke.myoyasing.presentation.featureUI.auth.login.LoginRoute
import com.karoke.myoyasing.presentation.featureUI.auth.otp.OtpVerificationRoute
import com.karoke.myoyasing.presentation.featureUI.home.DiscoverScreen
import com.karoke.myoyasing.presentation.featureUI.permissions.KaraokePermissionsScreen
import com.karoke.myoyasing.presentation.featureUI.recording.KaraokePlayerScreen
import com.karoke.myoyasing.presentation.feed.OyaFeedScreen
import com.karoke.myoyasing.presentation.publishing.PerformanceScreen
import com.karoke.myoyasing.presentation.publishing.PublishPerformanceScreen
import kotlinx.coroutines.launch


@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    var melodyComparisonResult by remember {
        mutableStateOf<MelodyComparisonResult?>(null)
    }
    var showSessionDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        SessionManager.sessionExpired.collect {

            showSessionDialog = true

        }
    }
    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {

        // Splash
        composable(AppRoutes.SPLASH) {
            OyaSingSplashScreen(
                onFinished = {
                    coroutineScope.launch {
                        val isLoggedIn = AppDataStore.getIsLogin()

                        if (isLoggedIn) {
                            navController.navigate(AppRoutes.MAIN) {
                                popUpTo(AppRoutes.SPLASH) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(AppRoutes.LOGIN) {
                                popUpTo(AppRoutes.SPLASH) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            )
        }

        // Login
        composable(AppRoutes.LOGIN) {
            LoginRoute(
                onNavigateToOtp = { countryCode, phoneNumber ->
                    navController.navigate(
                        AppRoutes.otp(countryCode, phoneNumber)
                    )
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }

        // OTP verification
        composable(
            route = AppRoutes.OTP,
            arguments = listOf(
                navArgument("countryCode") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("phoneNumber") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->

            val countryCode = backStackEntry.arguments
                ?.getString("countryCode")
                .orEmpty()

            val phoneNumber = backStackEntry.arguments
                ?.getString("phoneNumber")
                .orEmpty()

            OtpVerificationRoute(
                countryCode = countryCode,
                phoneNumber = phoneNumber,

                onVerified = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },

                onBack = {
                    navController.popBackStack(
                        AppRoutes.LOGIN,
                        inclusive = false
                    )
                },

                onEditPhone = {
                    navController.popBackStack(
                        AppRoutes.LOGIN,
                        inclusive = false
                    )
                },

                onContactSupport = {}
            )
        }

        // Main / Discover
        composable(AppRoutes.MAIN) {
            MainBottomNavScreen(navController) {
                DiscoverScreen(

                    onSingNowClick = { song ->
                        navController.navigate(
                            AppRoutes.karaokePermissions(song.id)
                        )
                    },
                    onSongClick = { song ->
                        navController.navigate(
                            AppRoutes.karaokePermissions(song.id)
                        )
                    },

                    onSearchClick = {},
                    onNotificationClick = {}
                )
            }
        }

        composable(AppRoutes.HOME) {
            MainBottomNavScreen(navController) {
                DiscoverScreen(

                    onSingNowClick = { song ->
                        navController.navigate(
                            AppRoutes.karaokePermissions(song.id)
                        )
                    },
                    onSongClick = { song ->
                        navController.navigate(
                            AppRoutes.karaokePermissions(song.id)
                        )
                    },
                    onSearchClick = {},
                    onNotificationClick = {}
                )
            }
        }



        composable(
            route = AppRoutes.KARAOKE_PERMISSIONS,
            arguments = listOf(
                navArgument("songId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val songId = backStackEntry.arguments
                ?.getString("songId")
                .orEmpty()

            KaraokePermissionsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSettingsClick = {

                },
                onMaybeLaterClick = {
                    navController.popBackStack()
                },
                onStartSingingClick = {
                    navController.navigate(
                        AppRoutes.recording(songId)
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }


        // Recording / Karaoke player
        composable(
            route = AppRoutes.RECORDING,
            arguments = listOf(
                navArgument("songId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val songId = backStackEntry.arguments
                ?.getString("songId")

            if (songId != null) {

                // Save the selected song ID for the Performance screen.
                backStackEntry.savedStateHandle["song_id"] = songId

                KaraokePlayerScreen(
                    songId = songId,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onDoneClick = { localPath ->

                        if (!localPath.isNullOrBlank()) {

                            backStackEntry.savedStateHandle[
                                "recording_path"
                            ] = localPath

                            navController.navigate(
                                AppRoutes.PERFORMANCE
                            ) {
                                launchSingleTop = true
                            }
                        }
                    },

                    onUploadSuccess = { uploadedMedia, localPath, comparisonResult , songTitle ->

                        android.util.Log.d(
                            "AppNavGraph",
                            "Media upload successful"
                        )

                        android.util.Log.d(
                            "AppNavGraph",
                            "Audio URL: ${uploadedMedia.audioUrl}"
                        )

                        android.util.Log.d(
                            "AppNavGraph",
                            "Video URL: ${uploadedMedia.videoUrl}"
                        )

                        android.util.Log.d(
                            "AppNavGraph",
                            "Thumbnail URL: ${uploadedMedia.thumbnailUrl}"
                        )

                        // Log the melody comparison result.
                        android.util.Log.d(
                            "AppNavGraph",
                            "Melody comparison result: $comparisonResult"
                        )
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedSongTitle", songTitle)
                        // Store the result so PerformanceScreen can display it.
                        melodyComparisonResult = comparisonResult

                        // Save the recording file path.
                        backStackEntry.savedStateHandle["recording_path"] =
                            localPath

                        // Save the uploaded media URLs.
                        backStackEntry.savedStateHandle["uploaded_audio_url"] =
                            uploadedMedia.audioUrl

                        backStackEntry.savedStateHandle["uploaded_video_url"] =
                            uploadedMedia.videoUrl

                        backStackEntry.savedStateHandle["uploaded_thumbnail_url"] =
                            uploadedMedia.thumbnailUrl

                        // Navigate to the performance screen.
                        navController.navigate(AppRoutes.PERFORMANCE) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(AppRoutes.FEED) {

            val userId by produceState<String?>(initialValue = null) {
                value = AppDataStore.getUserid()
            }

            MainBottomNavScreen(navController) {
                if (userId != null) {
                    OyaFeedScreen(
                        currentUserId = userId!!,
                        onDiscoverClick = {
                            navController.navigate(AppRoutes.MAIN)
                        },
                        onFeedClick = {},
                        onProfileClick = {
                            navController.navigate(AppRoutes.PROFILE)
                        }
                    )
                }
            }
        }


        composable(AppRoutes.PERFORMANCE) { backStackEntry ->

            val previousEntry = navController.previousBackStackEntry

            val recordingPath = previousEntry
                ?.savedStateHandle
                ?.get<String>("recording_path")

            val songIdString = previousEntry
                ?.savedStateHandle
                ?.get<String>("song_id")

            val audioUrl = previousEntry
                ?.savedStateHandle
                ?.get<String>("uploaded_audio_url")
                .orEmpty()

            val videoUrl = previousEntry
                ?.savedStateHandle
                ?.get<String>("uploaded_video_url")

            val thumbnailUrl = previousEntry
                ?.savedStateHandle
                ?.get<String>("uploaded_thumbnail_url")
            val songTitle = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("selectedSongTitle")
                .orEmpty()

            val songId = songIdString?.toIntOrNull()

            if (songId != null) {

                PerformanceScreen(
                    recordingPath = recordingPath,
                    songId = songId,
                    audioUrl = audioUrl,
                    videoUrl = videoUrl,
                    thumbnailUrl = thumbnailUrl,
                    songTitle=songTitle,

                    melodyComparisonResult = melodyComparisonResult,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSettingsClick = {},

                    onPublishClick = { performanceId ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedSongTitle", songTitle)
                        // Save the ID returned by POST /performances.
                        backStackEntry.savedStateHandle["created_performance_id"] =
                            performanceId

                        // Pass the recorded media to the next screen.
                        backStackEntry.savedStateHandle["publish_recording_path"] =
                            recordingPath

                        backStackEntry.savedStateHandle["publish_audio_url"] =
                            audioUrl

                        backStackEntry.savedStateHandle["publish_thumbnail_url"] =
                            thumbnailUrl

                        backStackEntry.savedStateHandle["publish_video_url"] =
                            videoUrl

                        android.util.Log.d(
                            "AppNavGraph",
                            "Created performance ID: $performanceId"
                        )

                        navController.navigate(AppRoutes.PUBLISHPERFORMANCE) {
                            launchSingleTop = true
                        }
                    },

                    onReRecordClick = {
                        navController.popBackStack()
                    },

                    onSaveDraftClick = {}
                )

            } else {

            }
        }

        // Publish performance
        composable(AppRoutes.PUBLISHPERFORMANCE) {

            val previousEntry = navController.previousBackStackEntry

            val performanceId = previousEntry
                ?.savedStateHandle
                ?.get<Int>("created_performance_id")

            val recordingPath = previousEntry
                ?.savedStateHandle
                ?.get<String>("publish_recording_path")

            val audioUrl = previousEntry
                ?.savedStateHandle
                ?.get<String>("publish_audio_url")

            val thumbnailUrl = previousEntry
                ?.savedStateHandle
                ?.get<String>("publish_thumbnail_url")

            val videoUrl = previousEntry
                ?.savedStateHandle
                ?.get<String>("publish_video_url")

            android.util.Log.d(
                "AppNavGraph",
                "Publish screen performance ID: $performanceId"
            )
            val songTitle = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("selectedSongTitle")
                .orEmpty()
            PublishPerformanceScreen(
                navController = navController,
                songTitle = songTitle,
                duration = "03:45",

                performanceId = performanceId,
                recordingPath = recordingPath,
                audioUrl = audioUrl,
                thumbnailUrl = thumbnailUrl,
                videoUrl = videoUrl,

                onBackClick = {
                    val returned = navController.popBackStack()

                    android.util.Log.d(
                        "AppNavGraph",
                        "Back navigation successful: $returned"
                    )
//                    val returned = navController.popBackStack(
//                        route = AppRoutes.PERFORMANCE,
//                        inclusive = false
//                    )
//
//                    android.util.Log.d(
//                        "AppNavGraph",
//                        "Returned to PerformanceScreen: $returned"
//                    )
                },

                onPreviewClick = {

                },

                onCollaboratorsClick = {},

                onAudienceClick = {},

                onPublishClick = { description ->

                    android.util.Log.d(
                        "AppNavGraph",
                        "Description: $description"
                    )
                }
            )
        }


        composable(AppRoutes.PROFILE) {
            MainBottomNavScreen(navController) {

            }
        }

    }

    if (showSessionDialog) {

        SessionExpiredDialog(
            onLoginClick = {

                showSessionDialog = false

                coroutineScope.launch {
                    AppDataStore.clearAccessToken()
                    AppDataStore.clearIsLogin()
                    navController.navigate(AppRoutes.LOGIN) {

                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }
        )
    }
}

