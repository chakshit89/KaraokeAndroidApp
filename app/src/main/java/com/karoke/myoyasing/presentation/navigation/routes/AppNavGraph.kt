package com.karoke.myoyasing.presentation.navigation.routes

import MainBottomNavScreen
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
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
//                    navController.navigate(AppRoutes.HOME)
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

                    onUploadSuccess = { uploadedMedia, localPath, comparisonResult, songTitle ->

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
            var showProgressDeleteDialog by rememberSaveable {
                mutableStateOf(false)
            }
            if (showProgressDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showProgressDeleteDialog = false
                    },
                    containerColor = Color(0xFF131313),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color(0xFFC863FB),
                        shape = RoundedCornerShape(16.dp)
                    ),
                    title = {
                        Text(
                            text = "Discard Progress",
                            color = Color.White
                        )
                    },
                    text = {
                        Text(
                            text = "Your current progress will be deleted. Do you want to continue?",
                            color = Color.White
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showProgressDeleteDialog = false

                                navController.navigate(AppRoutes.HOME) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }

                            }
                        ) {
                            Text(
                                text = "OK",
                                color = Color(0xFFC863FB)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showProgressDeleteDialog = false
                            }
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color.White
                            )
                        }
                    }
                )
            }

            val recordingEntry = navController.previousBackStackEntry

            val recordingPath = recordingEntry
                ?.savedStateHandle
                ?.get<String>("recording_path")

            val songIdString = recordingEntry
                ?.savedStateHandle
                ?.get<String>("song_id")

            val audioUrl = recordingEntry
                ?.savedStateHandle
                ?.get<String>("uploaded_audio_url")
                .orEmpty()

            val videoUrl = recordingEntry
                ?.savedStateHandle
                ?.get<String>("uploaded_video_url")

            val thumbnailUrl = recordingEntry
                ?.savedStateHandle
                ?.get<String>("uploaded_thumbnail_url")

            val songTitle = recordingEntry
                ?.savedStateHandle
                ?.get<String>("selectedSongTitle")
                .orEmpty()

            val songId = songIdString?.toIntOrNull()

            if (songId != null) {0
                PerformanceScreen(
                    recordingPath = recordingPath,
                    songId = songId,
                    audioUrl = audioUrl,
                    videoUrl = videoUrl,
                    thumbnailUrl = thumbnailUrl,
                    songTitle = songTitle,
                    melodyComparisonResult = melodyComparisonResult,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSettingsClick = { showProgressDeleteDialog = true },

                    onPublishClick = { performanceId ->

                        val alreadyOpened = backStackEntry.savedStateHandle
                            .get<Boolean>("publish_screen_opened") == true

                        if (!alreadyOpened) {

                            backStackEntry.savedStateHandle["publish_screen_opened"] = true

                            backStackEntry.savedStateHandle["created_performance_id"] =
                                performanceId

                            backStackEntry.savedStateHandle["publish_recording_path"] =
                                recordingPath

                            backStackEntry.savedStateHandle["publish_audio_url"] =
                                audioUrl

                            backStackEntry.savedStateHandle["publish_thumbnail_url"] =
                                thumbnailUrl

                            backStackEntry.savedStateHandle["publish_video_url"] =
                                videoUrl

                            backStackEntry.savedStateHandle["selectedSongTitle"] =
                                songTitle

                            android.util.Log.d(
                                "NavigationDebug",
                                "Opening Publish for performance ID: $performanceId"
                            )

                            navController.navigate(AppRoutes.PUBLISHPERFORMANCE) {
                                launchSingleTop = true
                            }
                        } else {
                            android.util.Log.d(
                                "NavigationDebug",
                                "Publish navigation already performed. Skipping."
                            )
                        }
                    },

                    onReRecordClick = {
                        navController.popBackStack()
                    },

                    onSaveDraftClick = {}
                )
            }
        }



        composable(AppRoutes.PUBLISHPERFORMANCE) {
            var showProgressDeleteDialog by rememberSaveable {
                mutableStateOf(false)
            }

            if (showProgressDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showProgressDeleteDialog = false
                    },
                    containerColor = Color(0xFF131313),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color(0xFFC863FB),
                        shape = RoundedCornerShape(16.dp)
                    ),
                    title = {
                        Text(
                            text = "Discard Progress",
                            color = Color.White
                        )
                    },
                    text = {
                        Text(
                            text = "Your current progress will be deleted. Do you want to continue?",
                            color = Color.White
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showProgressDeleteDialog = false

                                navController.navigate(AppRoutes.HOME) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }

                            }
                        ) {
                            Text(
                                text = "OK",
                                color = Color(0xFFC863FB)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showProgressDeleteDialog = false
                            }
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color.White
                            )
                        }
                    }
                )
            }
            val performanceEntry = remember(navController) {
                navController.getBackStackEntry(AppRoutes.PERFORMANCE)
            }

            val performanceId = performanceEntry.savedStateHandle
                .get<Int>("created_performance_id")

            val recordingPath = performanceEntry.savedStateHandle
                .get<String>("publish_recording_path")

            val audioUrl = performanceEntry.savedStateHandle
                .get<String>("publish_audio_url")

            val thumbnailUrl = performanceEntry.savedStateHandle
                .get<String>("publish_thumbnail_url")

            val videoUrl = performanceEntry.savedStateHandle
                .get<String>("publish_video_url")

            val songTitle = performanceEntry.savedStateHandle
                .get<String>("selectedSongTitle")
                .orEmpty()

            android.util.Log.d(
                "NavigationDebug",
                "Publish performance ID: $performanceId"
            )

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
                    android.util.Log.d(
                        "NavigationDebug",
                        "Back clicked. Current route: ${navController.currentBackStackEntry?.destination?.route}"
                    )

                    val returned = navController.popBackStack(
                        route = AppRoutes.PERFORMANCE,
                        inclusive = false
                    )

                    android.util.Log.d(
                        "NavigationDebug",
                        "Pop result: $returned"
                    )

                    android.util.Log.d(
                        "NavigationDebug",
                        "Route after pop: ${navController.currentBackStackEntry?.destination?.route}"
                    )
                },

                onPreviewClick = {},
                onCollaboratorsClick = {},
                onSettingsClick = { showProgressDeleteDialog = true },
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
                    AppDataStore.clearUserid()
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

