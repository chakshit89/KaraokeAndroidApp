package com.karoke.myoyasing.presentation.featureUI.recording

import android.Manifest
import android.content.pm.PackageManager

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.example.pitchmasterbeta.ui.workspace.PitchWave
import com.karoke.myoyasing.R
import com.karoke.myoyasing.core.utils.recordinglogic.KaraokeViewModel
import com.karoke.myoyasing.domain.model.MelodyComparisonResult
import com.karoke.myoyasing.domain.model.UploadedMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


private val BackgroundBlack = Color(0xFF0D0A10)
val Purple = Color(0xFFE4A1FF)
val PurpleBright = Color(0xFFD97AFF)
val PurpleDark = Color(0xFF542E64)
val TextWhite = Color(0xFFF7F3F8)
val TextMuted = Color(0xFF98929C)
private val CardColor = Color(0xFF242027)
private val Cyan = Color(0xFF76DFFF)


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun KaraokePlayerScreen(
    songId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onRestartClick: () -> Unit = {},
    onDoneClick: (String?) -> Unit = {},
    onUploadSuccess: (
        UploadedMedia,
        String,
        MelodyComparisonResult?,
        String
    ) -> Unit = { _, localPath, _, _ ->
        onDoneClick(localPath)
    }
) {
    val context = LocalContext.current

    val viewModel: KaraokeViewModel = viewModel()
    val songDetailViewModel: SongDetailViewModel = koinViewModel()

    val songDetailState by songDetailViewModel.uiState.collectAsState()
    val mediaUploadViewModel: MediaUploadViewModel = koinViewModel()

    val mediaUploadState by
    mediaUploadViewModel.uiState.collectAsState()

    val melodyComparisonViewModel: MelodyComparisonViewModel =
        koinViewModel()

    val melodyComparisonState by
    melodyComparisonViewModel.uiState.collectAsState()

    var melodyComparisonStarted by remember(songId) {
        mutableStateOf(false)}

    val song = songDetailState.song

    LaunchedEffect(songId) {
        songDetailViewModel.loadSong(songId)
    }
    LaunchedEffect(song?.instrumentalAudioUrl) {
        val instrumentalUrl = song?.instrumentalAudioUrl
            ?.takeIf { it.isNotBlank() }
            ?: return@LaunchedEffect

        android.util.Log.d(
            "KaraokePlayerScreen",
            "Starting instrumental preload: $instrumentalUrl"
        )

        viewModel.preloadInstrumental(instrumentalUrl)
    }
    LaunchedEffect(songDetailState.lyricsText) {
        songDetailState.lyricsText?.let(viewModel::loadLyrics)
    }
    var isFinishing by remember {
        mutableStateOf(false)
    }
    var recordingFilePath by remember {
        mutableStateOf<String?>(null)
    }

    val uiState by
    viewModel.uiState.collectAsState()

    val lifecycleOwner =
        LocalLifecycleOwner.current

    var isMusicPlaying by remember {
        mutableStateOf(false)
    }
    var showMicReminder by remember(songId) {
        mutableStateOf(false)
    }

    val voiceDetected = uiState.micAmplitude >= 0.045f
    val lyricsHaveStarted = uiState.currentLyricIndex >= 0

    val voiceStrength = (
            (uiState.micAmplitude - 0.025f) / (0.12f - 0.025f)
            ).coerceIn(0f, 1f)

    val dotMovement = 20f + voiceStrength * 100f

    val voiceTransition = rememberInfiniteTransition(
        label = "voice_indicator"
    )

    val dotPhase by voiceTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 300,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voice_dot_phase"
    )

    val dotOffset by animateDpAsState(
        targetValue = if (
            voiceDetected &&
            uiState.isRecording &&
            !isFinishing
        ) {
            (dotPhase * dotMovement).dp
        } else {
            0.dp
        },
        animationSpec = tween(durationMillis = 100),
        label = "voice_dot_offset"
    )
    var microphoneGranted by
    remember {

        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    val player =
        remember {

            ExoPlayer
                .Builder(context)
                .build()
        }
    fun startUploadAndComparison(
        finalFile: File,
        thumbnailUrl: String
    ) {
        val mp3File = File(
            finalFile.parentFile,
            "final_mix.mp3"
        )

        if (!mp3File.exists() || mp3File.length() == 0L) {
            android.util.Log.e(
                "KaraokePlayerScreen",
                "MP3 file is missing: ${mp3File.absolutePath}"
            )

            isFinishing = false
            return
        }

        // This matches the vocal file name used by your recorder:
        // user_vocals.wav -> user_vocals_melody.json
        val vocalFile = File(
            finalFile.parentFile,
            "user_vocals.wav"
        )

        val melodyFile = File(
            vocalFile.parentFile,
            "${vocalFile.nameWithoutExtension}_melody.json"
        )

        android.util.Log.d(
            "KaraokePlayerScreen",
            "Melody JSON: ${melodyFile.absolutePath}"
        )

        val originalSongId = songId.toLongOrNull()

        if (originalSongId == null) {
            android.util.Log.e(
                "KaraokePlayerScreen",
                "Invalid song ID: $songId"
            )

            isFinishing = false
            return
        }

        melodyComparisonViewModel.clearResult()
        melodyComparisonStarted = true

        melodyComparisonViewModel.compareMelody(
            songId = originalSongId,
            userMelodyPath = melodyFile.absolutePath
        )

        mediaUploadViewModel.uploadAudio(
            file = mp3File,
            thumbnailUrl = thumbnailUrl
        )
    }

    LaunchedEffect(
        mediaUploadState.uploadedMedia,
        melodyComparisonState.result,
        melodyComparisonState.errorMessage,
        melodyComparisonState.isLoading,
        melodyComparisonStarted
    ) {
        val uploadedMedia = mediaUploadState.uploadedMedia
            ?: return@LaunchedEffect

        val localPath = recordingFilePath
            ?: return@LaunchedEffect

        if (!melodyComparisonStarted) {
            return@LaunchedEffect
        }

        if (melodyComparisonState.isLoading) {
            return@LaunchedEffect
        }

        val comparisonFinished =
            melodyComparisonState.result != null ||
                    melodyComparisonState.errorMessage != null

        if (!comparisonFinished) {
            return@LaunchedEffect
        }
        cleanupAfterUpload(
            finalWavPath = localPath,
            instrumentalUrl = song?.instrumentalAudioUrl,
            context = context
        )
        onUploadSuccess(
            uploadedMedia,
            localPath,
            melodyComparisonState.result,
            song?.title.orEmpty()
        )

        mediaUploadViewModel.clearResult()
        melodyComparisonViewModel.clearResult()

        melodyComparisonStarted = false
    }

    LaunchedEffect(mediaUploadState.errorMessage) {
        if (mediaUploadState.errorMessage != null) {
            isFinishing = false
        }
    }
    DisposableEffect(player) {
        onDispose {
            viewModel.stopTracking()
            player.stop()
            player.release()
        }
    }

    var hasStartedPlayback by remember(songId) {
        mutableStateOf(false)
    }

    LaunchedEffect(microphoneGranted, song?.instrumentalAudioUrl) {
        val selectedSong = song ?: return@LaunchedEffect

        if (!microphoneGranted || hasStartedPlayback) {
            return@LaunchedEffect
        }

        try {
            player.setMediaItem(
                MediaItem.fromUri(selectedSong.instrumentalAudioUrl)
            )
            player.prepare()
            player.awaitReady()

            if (!currentCoroutineContext().isActive) {
                return@LaunchedEffect
            }

            if (hasStartedPlayback) {
                return@LaunchedEffect
            }

            val sessionId = System.currentTimeMillis()

            val performanceDir = File(
                context.filesDir,
                "performances/${selectedSong.assetCode}_$sessionId"
            ).apply {
                if (!exists() && !mkdirs()) {
                    throw IllegalStateException(
                        "Could not create performance directory"
                    )
                }
            }

            val recordingFile = File(
                performanceDir,
                "user_vocals.wav"
            )

            viewModel.startRecording(recordingFile)

            player.seekTo(0)
            player.play()
            hasStartedPlayback = true

            viewModel.startTracking(
                getPosition = { player.currentPosition },
                getDuration = {
                    player.duration.takeIf { it > 0 } ?: 0L
                }
            )
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            viewModel.stopTracking()
            viewModel.stopRecording()
        }
    }
    LaunchedEffect(
        lyricsHaveStarted,
        voiceDetected,
        isMusicPlaying,
        uiState.isRecording,
        isFinishing
    ) {
        if (
            lyricsHaveStarted &&
            isMusicPlaying &&
            uiState.isRecording &&
            !isFinishing &&
            !voiceDetected
        ) {
            delay(30_000L)


            showMicReminder = true
        } else {
            showMicReminder = false
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                isMusicPlaying = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState != Player.STATE_ENDED || isFinishing) return

                val instrumentalUrl = song?.instrumentalAudioUrl ?: run {
                    isFinishing = false
                    return
                }

                val thumbnailUrl = song?.coverImageUrl

                isFinishing = true
                isMusicPlaying = false

                viewModel.stopTracking()
                player.pause()
                player.stop()

                viewModel.finishRecording(
                    instrumentalUrl = instrumentalUrl
                ) { finalFile ->
                    if (finalFile != null && finalFile.exists()) {
                        recordingFilePath = finalFile.absolutePath

//                        onDoneClick(finalFile.absolutePath)

                        if (!thumbnailUrl.isNullOrBlank()) {
                            val mp3File = File(
                                finalFile.parentFile,
                                "final_mix.mp3"
                            )

                            if (!mp3File.exists() || mp3File.length() == 0L) {
                                android.util.Log.e(
                                    "KaraokePlayerScreen",
                                    "MP3 file is missing or empty: ${mp3File.absolutePath}"
                                )
                                isFinishing = false
                                return@finishRecording
                            }

                            startUploadAndComparison(
                                finalFile = finalFile,
                                thumbnailUrl = thumbnailUrl
                            )
                        }
                    } else {
                        isFinishing = false
                    }
                }
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
        }
    }


    DisposableEffect(
        lifecycleOwner,
        player
    ) {

        val observer =
            LifecycleEventObserver {
                    _,
                    event
                ->

                when (event) {

                    Lifecycle.Event.ON_PAUSE -> {

                        player.pause()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        val hasMicPermission =
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                        microphoneGranted = hasMicPermission

                        val currentState = viewModel.uiState.value

                        if (
                            hasMicPermission &&
                            currentState.isRecording &&
                            !currentState.isProcessing &&
                            player.playbackState != Player.STATE_ENDED
                        ) {
                            player.play()
                        }
                    }

                    Lifecycle.Event.ON_STOP -> {

                        viewModel.stopTracking()

                        if (
                            viewModel
                                .uiState
                                .value
                                .isRecording
                        ) {

                            viewModel.stopRecording()
                        }

                        player.pause()
                    }

                    else -> Unit
                }
            }

        lifecycleOwner
            .lifecycle
            .addObserver(
                observer
            )

        onDispose {

            lifecycleOwner
                .lifecycle
                .removeObserver(
                    observer
                )
        }
    }

    var isCoverLoading by remember(song?.coverImageUrl) {
        mutableStateOf(!song?.coverImageUrl.isNullOrBlank())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(BackgroundBlack)
    ) {
        AsyncImage(
            model = song?.coverImageUrl,
            contentDescription = song?.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            placeholder = painterResource(R.drawable.karaoke_singer),
            error = painterResource(R.drawable.karaoke_singer),
            onLoading = {
                isCoverLoading = true
            },
            onSuccess = {
                isCoverLoading = false
            },
            onError = {
                isCoverLoading = false
            }
        )

        if (isCoverLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = PurpleBright,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeWidth = 4.dp
                )
            }
        }





        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color(0xD80D0910),
                                    Color(0xA80E0A12),
                                    Color(0xCC0B0910),
                                    Color(0xF20A0810)
                                )
                        )
                    )
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.statusBars
                    )
        ) {

            TopBar(
                onBackClick = {

                    if (
                        uiState.isProcessing
                    ) {
                        return@TopBar
                    }

                    viewModel.stopTracking()

                    viewModel.stopRecording()

                    player.stop()

                    onBackClick()
                },
                onSettingsClick =
                    onSettingsClick
            )

            ProgressSection(
                positionMs =
                    uiState.positionMs,
                durationMs =
                    uiState.durationMs
            )

            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                150.dp
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    PitchWave(isPlaying = isMusicPlaying)
                    Box(
                        modifier = Modifier
                            .offset(y = dotOffset)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(PurpleBright)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )

                LyricsSection(
                    lyrics =
                        uiState.lyrics,
                    currentLyricIndex =
                        uiState.currentLyricIndex,
                    positionMs =
                        uiState.positionMs,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )

                BottomButtons(
                    onRestartClick = {

                        if (
                            uiState.isProcessing
                        ) {
                            return@BottomButtons
                        }

                        viewModel.stopTracking()

                        player.pause()

                        player.seekTo(
                            0
                        )

                        val recordingFile = File(
                            context.cacheDir,
                            "${song?.assetCode ?: songId}_${System.currentTimeMillis()}.wav"
                        )

                        viewModel.restartRecording(
                            file =
                                recordingFile
                        ) {

                            player.seekTo(
                                0
                            )

                            player.play()

                            viewModel.startTracking(
                                getPosition = {
                                    player.currentPosition
                                },
                                getDuration = {
                                    player.duration
                                        .takeIf {
                                            it > 0
                                        }
                                        ?: 0L
                                }
                            )

                            onRestartClick()
                        }
                    },

                    onDoneClick = {
                        if (isFinishing || mediaUploadState.isUploading) {
                            return@BottomButtons
                        }

                        val instrumentalUrl = song?.instrumentalAudioUrl
                            ?: return@BottomButtons

                        val thumbnailUrl = song?.coverImageUrl

                        if (thumbnailUrl.isNullOrBlank()) {
                            return@BottomButtons
                        }

                        isFinishing = true

                        viewModel.stopTracking()
                        player.pause()
                        player.stop()

                        viewModel.finishRecording(
                            instrumentalUrl = instrumentalUrl
                        ) { finalFile ->

                            if (
                                finalFile == null ||
                                !finalFile.exists() ||
                                finalFile.length() <= 44L
                            ) {
                                isFinishing = false

                                android.util.Log.e(
                                    "KaraokePlayerScreen",
                                    "Final mixed audio file is missing or empty"
                                )

                                return@finishRecording
                            }


                            recordingFilePath = finalFile.absolutePath

                            val mp3File = File(
                                finalFile.parentFile,
                                "final_mix.mp3"
                            )

                            if (!mp3File.exists() || mp3File.length() == 0L) {
                                android.util.Log.e(
                                    "KaraokePlayerScreen",
                                    "MP3 file is missing or empty: ${mp3File.absolutePath}"
                                )

                                isFinishing = false
                                return@finishRecording
                            }

                            startUploadAndComparison(
                                finalFile = finalFile,
                                thumbnailUrl = thumbnailUrl
                            )
                        }
                    }
                )

                Spacer(
                    modifier =
                        Modifier
                            .height(
                                12.dp
                            )
                            .navigationBarsPadding())
            }
        }

        if (
            uiState.isProcessing ||
            mediaUploadState.isUploading ||
            melodyComparisonState.isLoading
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha =
                                    0.78f
                            )
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator(
                        color =
                            PurpleBright
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                18.dp
                            )
                    )

                    Text(
                        text = when {
                            uiState.isProcessing ->
                                "Preparing your performance..."

                            melodyComparisonState.isLoading &&
                                    mediaUploadState.isUploading ->
                                "Uploading and scoring your performance..."

                            melodyComparisonState.isLoading ->
                                "Analyzing your singing performance..."

                            mediaUploadState.isUploading ->
                                "Uploading your performance..."

                            else ->
                                "Calculating Score..."
                        },
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )

                    Text(
                        text =
                            "Mixing your voice with the music",
                        color =
                            TextMuted,
                        fontSize =
                            13.sp
                    )
                }
            }
        }

        if (
            !uiState.isProcessing &&
            !mediaUploadState.isUploading &&
            !melodyComparisonState.isLoading &&
            (
                    uiState.errorMessage != null ||
                            mediaUploadState.errorMessage != null ||
                            melodyComparisonState.errorMessage != null
                    )
        ) {
            val errorMessage = mediaUploadState.errorMessage
                ?: uiState.errorMessage
                ?: melodyComparisonState.errorMessage
                ?: ""

            val canRestart = errorMessage.contains(
                "No vocal recording was created",
                ignoreCase = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 90.dp
                    )
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF3A1720))
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = errorMessage,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                if (canRestart) {
                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            val selectedSong = song ?: return@TextButton
                            val instrumentalUrl =
                                selectedSong.instrumentalAudioUrl

                            // Reset the error and restart the recording.
                            isFinishing = false
                            showMicReminder = false

                            viewModel.stopTracking()
                            player.pause()

                            // Prepare the instrumental again.
                            player.setMediaItem(
                                MediaItem.fromUri(instrumentalUrl)
                            )
                            player.prepare()

                            // Create a fresh recording session.
                            val sessionDir = File(
                                context.filesDir,
                                "performances/" +
                                        "${selectedSong.assetCode}_" +
                                        System.currentTimeMillis()
                            )

                            if (!sessionDir.exists() &&
                                !sessionDir.mkdirs()
                            ) {
                                android.util.Log.e(
                                    "KaraokePlayerScreen",
                                    "Could not create recording directory"
                                )
                                return@TextButton
                            }

                            val recordingFile = File(
                                sessionDir,
                                "user_vocals.wav"
                            )

                            viewModel.restartRecording(
                                file = recordingFile
                            ) {
                                recordingFilePath = null

                                player.seekTo(0)
                                player.play()

                                hasStartedPlayback = true

                                viewModel.startTracking(
                                    getPosition = {
                                        player.currentPosition
                                    },
                                    getDuration = {
                                        player.duration
                                            .takeIf { it > 0 }
                                            ?: 0L
                                    }
                                )
                            }
                        }
                    ) {
                        Text(
                            text = "Start",
                            color = PurpleBright,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (songDetailState.isLoading && song == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC0D0A10)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = PurpleBright
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Loading song...",
                        color = TextWhite
                    )
                }
            }
        }

        if (!songDetailState.isLoading && song == null &&
            songDetailState.errorMessage != null
        ) {
            Text(
                text = songDetailState.errorMessage ?: "Unable to load song.",
                color = Color(0xFFFF7777),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(24.dp)
            )
        }

    }
    if (showMicReminder) {
        AlertDialog(
            onDismissRequest = {
                showMicReminder = false
            },
            title = {
                Text(
                    text = "Are you singing?",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "We can't hear your voice yet. " +
                            "Turn on your microphone, sing along " +
                            "with the lyrics, otherwise your final scoring will effect!",
                    color = TextMuted,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMicReminder = false
                    }
                ) {
                    Text(
                        text = "Got it",
                        color = PurpleBright
                    )
                }
            },
            containerColor = Color(0xFF21182B),
            titleContentColor = Color.White,
            textContentColor = TextMuted
        )
    }
}

private  suspend fun cleanupAfterUpload(
    finalWavPath: String?,
    instrumentalUrl: String?,
    context: android.content.Context
) = withContext(Dispatchers.IO) {

    val sessionDir = finalWavPath
        ?.takeIf { it.isNotBlank() }
        ?.let { File(it).parentFile }

    if (sessionDir != null && sessionDir.exists()) {

        // Delete the uploaded MP3 copy.
        File(sessionDir, "final_mix.mp3").delete()

        // Delete the separate vocal recording.
        File(sessionDir, "user_vocals.wav").delete()

        // Delete the melody JSON after comparison has finished.
        File(sessionDir, "user_vocals_melody.json").delete()

        // Delete any leftover temporary PCM file.
        File(sessionDir, "mixed_tmp.pcm").delete()
    }

    // Delete only the cached instrumental belonging to this URL.
    if (!instrumentalUrl.isNullOrBlank()) {
        val cacheName =
            "instrumental_${instrumentalUrl.hashCode().toUInt().toString(16)}.mp3"

        File(context.cacheDir, cacheName).delete()
    }


}


private suspend fun ExoPlayer.awaitReady() {
    if (playbackState == Player.STATE_READY) return

    suspendCancellableCoroutine { continuation ->
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    removeListener(this)
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                removeListener(this)
                if (continuation.isActive) {
                    continuation.resumeWithException(error)
                }
            }
        }

        addListener(listener)

        continuation.invokeOnCancellation {
            removeListener(listener)
        }

        if (playbackState == Player.STATE_READY) {
            removeListener(listener)
            if (continuation.isActive) {
                continuation.resume(Unit)
            }
        }
    }

}