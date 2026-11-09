package com.karoke.myoyasing.presentation.publishing

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.karoke.myoyasing.R
import com.karoke.myoyasing.core.utils.MediaUrlHelper
import com.karoke.myoyasing.core.utils.PerformanceSoundManager
import com.karoke.myoyasing.domain.model.MelodyComparisonResult
import com.karoke.myoyasing.presentation.publishing.viewmodels.CreatePerformanceViewModel
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.rememberKonfettiState
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


private val Background = Color(0xFF0D0B0E)
private val SurfaceDark = Color(0xFF181519)
private val SurfaceLight = Color(0xFF211D22)

private val NeonPurple = Color(0xFFD88AFF)
private val LightPurple = Color(0xFFE4A5FF)
private val Cyan = Color(0xFF70E5FF)

private val TextPrimary = Color(0xFFF6F0F7)
private val TextSecondary = Color(0xFFB8AFBA)
private val BorderColor = Color(0xFF3D3540)
private val SoraFontFamily = FontFamily(
    Font(R.font.sora_regular, FontWeight.Normal)
)

@Composable
fun PerformanceScreen(
    recordingPath: String?,
    songId: Int,
    audioUrl: String,
    videoUrl: String?,
    thumbnailUrl: String?,
    songTitle: String,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onReRecordClick: () -> Unit = {},
    onSaveDraftClick: () -> Unit = {},
    onPublishClick: (Int) -> Unit = {},
    melodyComparisonResult: MelodyComparisonResult? = null
) {
    if (LocalInspectionMode.current) {
        PerformanceScreenContent(
            recordingPath = recordingPath,
            audioUrl = audioUrl,
            thumbnailUrl = thumbnailUrl,
            melodyComparisonResult = melodyComparisonResult,
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onReRecordClick = onReRecordClick,
            onSaveDraftClick = onSaveDraftClick,
            onSubmitPerformance = {}
        )
        return
    }

    val performanceViewModel: CreatePerformanceViewModel =
        org.koin.androidx.compose.koinViewModel()

    val performanceState by
    performanceViewModel.uiState.collectAsState()

    val submitPerformance = {
        val result = melodyComparisonResult

        performanceViewModel.publishPerformance(
            songId = songId,
            audioUrl = audioUrl,
            videoUrl = videoUrl,
            score = result?.finalScore?.roundToInt() ?: 0,
            grade = result?.grade ?: "",
            pitchAccuracy = result?.pitchScore?.roundToInt() ?: 0,
            timingAccuracy = result?.timingScore?.roundToInt() ?: 0
        )
    }


    LaunchedEffect(
        performanceState.isPublished,
        performanceState.performanceId
    ) {
        if (performanceState.isPublished) {
            val id = performanceState.performanceId
                ?: return@LaunchedEffect

            onPublishClick(id)
        }
    }

    PerformanceScreenContent(
        recordingPath = recordingPath,
        thumbnailUrl = thumbnailUrl,
        melodyComparisonResult = melodyComparisonResult,
        audioUrl = audioUrl,
        onBackClick = onBackClick,
        onSettingsClick = onSettingsClick,
        onReRecordClick = onReRecordClick,
        onSaveDraftClick = onSaveDraftClick,
        onSubmitPerformance = submitPerformance
    )
}

@Composable
fun PerformanceScreenContent(
    recordingPath: String?,
    audioUrl: String?,
    thumbnailUrl: String?,
    melodyComparisonResult: MelodyComparisonResult?,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onReRecordClick: () -> Unit = {},
    onSaveDraftClick: () -> Unit = {},
    onSubmitPerformance: () -> Unit = {}
) {

    val context = LocalContext.current

    val soundManager = remember {
        PerformanceSoundManager(context)
    }

    DisposableEffect(soundManager) {
        onDispose {
            soundManager.release()
        }
    }
//    val grade = when {
//        melodyComparisonResult?.finalScore!! <= 1 -> "S"
//        melodyComparisonResult?.finalScore!! <= 2 -> "A+"
//        melodyComparisonResult?.finalScore!! <= 3 -> "A"
//        melodyComparisonResult?.finalScore!! <= 4 -> "B"
//        melodyComparisonResult?.finalScore!! <= 5 -> "C"
//        else -> "D"
//    }
    Log.e("KonfettiStategrade", "PerformanceScreenContent: ")
    val grade = melodyComparisonResult?.grade
        ?.trim()
        ?.uppercase()

    val konfettiState = rememberKonfettiState()

    val celebrationParty = remember(grade) {

        when (grade) {

            "S" -> Party(
                speed = 25f,
                maxSpeed = 55f,
                damping = 0.9f,
                spread = 360,
                timeToLive = 3500L,
                colors = listOf(
                    0x9BE7A5,
                    0xFFD700,
                    0xFFFFFF,
                    0x70E5FF,
                    0xD88AFF
                ),
                position = Position.Relative(0.5, 0.25),
                emitter = Emitter(
                    duration = 1000,
                    TimeUnit.MILLISECONDS
                ).max(220)
            )

            "A+" -> Party(
                speed = 20f,
                maxSpeed = 45f,
                damping = 0.9f,
                spread = 300,
                timeToLive = 3000L,
                colors = listOf(
                    0x9BE7A5,
                    0xFFD700,
                    0xFFFFFF,
                    0x70E5FF
                ),
                position = Position.Relative(0.5, 0.25),
                emitter = Emitter(
                    duration = 700,
                    TimeUnit.MILLISECONDS
                ).max(140)
            )

            "A" -> Party(
                speed = 15f,
                maxSpeed = 35f,
                damping = 0.9f,
                spread = 260,
                timeToLive = 2500L,
                colors = listOf(
                    0x9BE7A5,
                    0xFFFFFF,
                    0xD88AFF
                ),
                position = Position.Relative(0.5, 0.25),
                emitter = Emitter(
                    duration = 500,
                    TimeUnit.MILLISECONDS
                ).max(80)
            )

            else -> null
        }
    }
    LaunchedEffect(grade) {

        kotlinx.coroutines.delay(1000L)

        Log.d("PerformanceSound", "Triggering sound for grade: $grade")

        soundManager.playForGrade(grade)

        celebrationParty?.let { party ->
            konfettiState.start(party)
        }
    }



    Surface(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        color = Background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = WindowInsets.statusBars
                            .asPaddingValues()
                            .calculateTopPadding()
                    )
            ) {
                PerformanceTopBar(
                    onBackClick = onBackClick,
                    onSettingsClick = onSettingsClick
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 24.dp,
                            bottom = 32.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        PerformanceScore(
                            result = melodyComparisonResult
                        )
                    }

                    item {
                        PerformancePreview(
                            recordingPath = recordingPath,
                            audioUrl = audioUrl,
                            thumbnailUrl = thumbnailUrl
                        )
                    }

                    item {
                        ActionButtons(
                            onReRecordClick = onReRecordClick,
                            onSaveDraftClick = {
                                onSubmitPerformance()
                            }
                        )
                    }

                    item {
                        PublishButton(
                            onClick = {
                                onSubmitPerformance()
                            }
                        )
                    }

                    item {
                        PerformanceHighlights(
                            result = melodyComparisonResult
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(80.dp)
                        )
                    }
                }
            }

            KonfettiView(
                state = konfettiState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
private fun PerformanceTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF151216))
            .padding(horizontal = 18.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = LightPurple,
                modifier = Modifier.size(25.dp)
            )
        }

        Text(
            text = "Review Performance",
            color = LightPurple,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun PerformanceScore(
    result: MelodyComparisonResult?
) {
    val finalScore = result?.finalScore?.let {
        String.format(java.util.Locale.US, "%.2f", it)
    } ?: "--"

    val grade = result?.grade?.trim()?.uppercase() ?: ""
//    val grade = when {
//        result?.finalScore!! <= 1 -> "S"
//        result?.finalScore!! <= 2 -> "A+"
//        result?.finalScore!! <= 3 -> "A"
//        result?.finalScore!! <= 4 -> "B"
//        result?.finalScore!! <= 5 -> "C"
//        else -> "D"
//    }

    val (gradeColor, achievementText) = when (grade) {

        "S" -> Color(0xFF9BE7A5) to "Excellent"

        "A+" -> Color(0xFF9BE7A5) to "Very Good"

        "A" -> Color(0xFF9BE7A5) to "Good"

        "B" -> Color(0xFFFFB74D) to "Average"

        "C" -> Color(0xFFFFB74D) to "Below Average"

        "D" -> Color(0xFFFF5252) to "Poor"

        "E" -> Color(0xFFFF5252) to "Very Poor"

        else -> NeonPurple to "Performance"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = achievementText.uppercase(),
            color = gradeColor,
            fontSize = 15.sp,
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Score circle
        Box(
            modifier = Modifier.size(158.dp),
            contentAlignment = Alignment.Center
        ) {


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 7.dp,
                        color = gradeColor,
                        shape = CircleShape
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = finalScore,
                        color = gradeColor,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "/10",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "GRADE ${grade.ifEmpty { "--" }}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = result?.songTitle ?: "Performance Score",
            color = TextSecondary,
            fontFamily = SoraFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "P E R F O R M A N C E   S C O R E",
            color = TextSecondary,
            fontFamily = SoraFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.8.sp
        )

        result?.message?.let { message ->

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = TextSecondary,
                fontFamily = SoraFontFamily,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@androidx.annotation.OptIn(
    androidx.media3.common.util.UnstableApi::class
)
@Composable
private fun PerformancePreview(
    recordingPath: String?,
    audioUrl: String?,
    thumbnailUrl: String?
) {

    val context = LocalContext.current

    val fullAudioUrl = remember(audioUrl) {
        MediaUrlHelper.getFullUrl(audioUrl)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    val player = remember(context) {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                android.util.Log.d(
                    "PerformancePreview",
                    "Playback state: $playbackState"
                )
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                android.util.Log.e(
                    "PerformancePreview",
                    "Audio playback failed: ${error.message}",
                    error
                )
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, fullAudioUrl) {
        isPlaying = false
        player.stop()
        player.clearMediaItems()

        if (!fullAudioUrl.isNullOrBlank()) {
            android.util.Log.d(
                "PerformancePreview",
                "Loading audio URL: $fullAudioUrl"
            )

            player.setMediaItem(
                MediaItem.fromUri(fullAudioUrl)
            )
            player.prepare()
        } else {
            android.util.Log.e(
                "PerformancePreview",
                "Audio URL is null or empty. Original URL: $audioUrl"
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(
        label = "neonBorder"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "neonRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .padding(4.dp)
    ) {
        // Animated neon border
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
        ) {
            val borderWidth = 4.dp.toPx()
            val inset = borderWidth / 2
            val cornerRadius = 10.dp.toPx()

            val brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFF00F5FF),
                    Color(0xFF8A2BE2),
                    Color(0xFFFF00FF),
                    Color(0xFFFF1493),
                    Color(0xFF8A2BE2),
                    Color(0xFF00F5FF)
                ),
                center = Offset(
                    size.width / 2,
                    size.height / 2
                )
            )

            rotate(
                degrees = rotation,
                pivot = Offset(
                    size.width / 2,
                    size.height / 2
                )
            ) {
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(
                        size.width - borderWidth,
                        size.height - borderWidth
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        cornerRadius
                    ),
                    style = Stroke(width = borderWidth)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            val fullThumbnailUrl = remember(thumbnailUrl) {
                MediaUrlHelper.getFullUrl(thumbnailUrl)
            }

            AsyncImage(
                model = fullThumbnailUrl,
                contentDescription = "Performance thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.karaoke_singer),
                error = painterResource(R.drawable.karaoke_singer)
            )


            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable(
                        enabled = !fullAudioUrl.isNullOrBlank()
                    ) {
                        if (player.isPlaying) {
                            player.pause()
                        } else {
                            if (player.playbackState == Player.STATE_ENDED) {
                                player.seekTo(0)
                            }

                            player.play()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}


@Composable
private fun ActionButtons(
    onReRecordClick: () -> Unit,
    onSaveDraftClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        PerformanceActionButton(
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null,
                    tint = Cyan,
                    modifier = Modifier.size(22.dp)
                )
            },
            title = "Re-record",
            onClick = onReRecordClick
        )

//        PerformanceActionButton(
//            modifier = Modifier.weight(1f),
//            icon = {
//                Icon(
//                    imageVector = Icons.Default.Description,
//                    contentDescription = null,
//                    tint = LightPurple,
//                    modifier = Modifier.size(22.dp)
//                )
//            },
//            title = "Save Draft",
//            onClick = onSaveDraftClick
//        )
    }
}


@Composable
private fun PerformanceActionButton(
    modifier: Modifier,
    icon: @Composable () -> Unit,
    title: String,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier
            .height(76.dp)
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceDark,
        onClick = onClick
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            icon()

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = title,
                fontFamily = SoraFontFamily,
                color = TextPrimary,
                fontSize = 13.sp
            )
        }
    }
}


@Composable
private fun PublishButton(
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = LightPurple,
        onClick = onClick
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = Color(0xFF6C167E),
                modifier = Modifier.size(24.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text = "PUBLISH PERFORMANCE",
                color = Color(0xFF68137C),
                fontSize = 15.sp,
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}


@Composable
private fun PerformanceHighlights(
    result: MelodyComparisonResult?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "PERFORMANCE HIGHLIGHTS",
            color = Color(0xFF827985),
            fontSize = 11.sp,
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.3.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        HighlightItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = Cyan,
                    modifier = Modifier.size(23.dp)
                )
            },
            title = "Pitch Accuracy",
            score = result?.let {
                "${it.pitchScore}%"
            } ?: "--"
        )

        Spacer(modifier = Modifier.height(10.dp))

        HighlightItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = Cyan,
                    modifier = Modifier.size(23.dp)
                )
            },
            title = "Timing & Rhythm",
            score = result?.let {
                "${it.timingScore}%"
            } ?: "--"
        )

        Spacer(modifier = Modifier.height(10.dp))

        HighlightItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = LightPurple,
                    modifier = Modifier.size(23.dp)
                )
            },
            title = "Completeness",
            score = result?.let {
                "${it.completenessScore}%"
            } ?: "--"
        )
    }
}


@Composable
private fun HighlightItem(
    icon: @Composable () -> Unit,
    title: String,
    score: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(SurfaceDark)
            .border(
                1.dp,
                BorderColor,
                RoundedCornerShape(9.dp)
            )
            .padding(horizontal = 16.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        icon()

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Text(
            text = title,
            color = TextPrimary,
            fontFamily = SoraFontFamily,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = score,
            color = LightPurple,
            fontSize = 16.sp
        )
    }
}


//@Preview(
//    name = "Performance Screen",
//    showBackground = true,
//    showSystemUi = true,
//    widthDp = 360,
//    heightDp = 800
//)
//@Composable
//private fun PerformanceScreenPreview() {
//    CompositionLocalProvider(
//        LocalInspectionMode provides true
//    ) {
//        MaterialTheme {
//            PerformanceScreen(
//                recordingPath = null,
//                songId = 1,
//                audioUrl = "",
//                videoUrl = null,
//                thumbnailUrl = null,
//                onBackClick = {},
//                onSettingsClick = {},
//                onReRecordClick = {},
//                onSaveDraftClick = {},
//                onPublishClick = {}
//            )
//        }
//    }
//}