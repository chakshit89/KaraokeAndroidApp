package com.karoke.myoyasing.presentation.featureUI.Homesong

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

private val PlayerPurple = Color(0xFFD52CFF)
private val PlayerPurpleDark = Color(0xFF8A16C9)
private val PlayerNeonPink = Color(0xFFE94DFF)

private val PlayerBackground = Color(0xE6100B17)
private val PlayerMuted = Color(0xFFB6AFBD)
private val PlayerWaveInactive = Color(0xFF403A47)

private val PlayerGlassTop = Color(0xD92A1734)
private val PlayerGlassMiddle = Color(0xD918101F)
private val PlayerGlassBottom = Color(0xE60F0B16)

private val PlayerBorderGlow = Color(0xFFD936FF)
private val PlayerTrack = Color(0xFF514B58)
private val PlayerCloseBackground = Color(0xCC302838)

@Composable
fun MiniPlayerDialog(
    controller: SongPreviewController,
    songTitle: String,
    artistName: String,
    thumbnail: Painter,
    audioUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    // Read the actual ExoPlayer position continuously.
    LaunchedEffect(controller) {
        while (true) {
            controller.updateProgress()

            if (!isSeeking) {
                sliderPosition = controller.playbackPosition.toFloat()
            }

            delay(100L)
        }
    }

    val duration = controller.playbackDuration
    val position = if (isSeeking) {
        sliderPosition.toLong()
    } else {
        controller.playbackPosition
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 35.dp,
                        shape = RoundedCornerShape(38.dp),
                        ambientColor = PlayerPurple.copy(alpha = 0.38f),
                        spotColor = PlayerNeonPink.copy(alpha = 0.48f)
                    )
                    .clip(RoundedCornerShape(38.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PlayerGlassTop,
                                PlayerGlassMiddle,
                                PlayerGlassBottom
                            )
                        )
                    )
                    .border(
                        width = 1.8.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE957FF),
                                Color(0xFFB51DFF),
                                Color(0xFF7C159E),
                                Color(0xFFE957FF)
                            )
                        ),
                        shape = RoundedCornerShape(38.dp)
                    )
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 14.dp,
                        bottom = 24.dp
                    )
            ) {

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                        .width(48.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(PlayerPurple.copy(alpha = 0.48f))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(y = (-5).dp)
                            .clip(CircleShape)
                            .shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                                ambientColor = PlayerPurple.copy(alpha = 0.18f),
                                spotColor = PlayerPurple.copy(alpha = 0.25f)
                            )
                            .background(PlayerCloseBackground)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close mini player",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(156.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    Box(
                        modifier = Modifier
                            .weight(1.05f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Draw a CD/vinyl record without needing another image.
                        VinylRecord(
                            modifier = Modifier
                                .padding(start = 68.dp)
                                .size(108.dp)
                        )

                        Image(
                            painter = thumbnail,
                            contentDescription = "$songTitle album artwork",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.86f)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier
                            .weight(1.45f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = songTitle,
                            color = Color.White,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = artistName,
                            color = PlayerMuted,
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(17.dp))


                        PlaybackWaveform(
                            progress = if (duration > 0L) {
                                position.toFloat() / duration.toFloat()
                            } else {
                                0f
                            },
                            isPlaying = controller.isPlaying,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive seek bar
                Slider(
                    value = sliderPosition.coerceIn(
                        0f,
                        duration.coerceAtLeast(1L).toFloat()
                    ),
                    onValueChange = { newPosition ->
                        isSeeking = true
                        sliderPosition = newPosition
                    },
                    onValueChangeFinished = {
                        controller.seekTo(sliderPosition.toLong())
                        isSeeking = false
                    },
                    valueRange = 0f..duration.coerceAtLeast(1L).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFEAC7F7),
                        activeTrackColor = PlayerNeonPink,
                        inactiveTrackColor = PlayerTrack,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatPlayerTime(position),
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    Text(
                        text = formatPlayerTime(duration),
                        color = PlayerMuted,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    PlayerSideButton(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download song",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        onClick = {
                            downloadSong(
                                context = context,
                                songTitle = songTitle,
                                audioUrl = audioUrl
                            )
                        }
                    )

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(
                                elevation = 24.dp,
                                shape = CircleShape,
                                ambientColor = PlayerPurple.copy(alpha = 0.65f),
                                spotColor = PlayerNeonPink.copy(alpha = 0.75f)
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF9D24D6),
                                        Color(0xFF70129F),
                                        Color(0xFF561080)
                                    )
                                )
                            )
                            .border(
                                width = 3.dp,
                                color = Color(0xFFE15BFF),
                                shape = CircleShape
                            )
                            .clickable {
                                if (controller.isPlaying) {
                                    controller.pause()
                                } else {
                                    controller.resume()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (controller.isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = if (controller.isPlaying) {
                                "Pause"
                            } else {
                                "Play"
                            },
                            tint = Color.White,
                            modifier = Modifier.size(39.dp)
                        )
                    }


                    PlayerSideButton(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share song",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        onClick = {
                            shareSong(
                                context = context,
                                songTitle = songTitle,
                                artistName = artistName,
                                audioUrl = audioUrl
                            )
                        }
                    )
                }

            }
        }
    }
}

@Composable
private fun PlayerSideButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun VinylRecord(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = diameter / 2f

        // Black disc
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF424047),
                    Color(0xFF171719),
                    Color(0xFF050506),
                    Color(0xFF242126)
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )

        // Vinyl grooves
        for (i in 1..9) {
            val grooveRadius = radius * (0.35f + i * 0.061f)

            if (grooveRadius < radius * 0.96f) {
                drawCircle(
                    color = Color.White.copy(
                        alpha = if (i % 2 == 0) 0.10f else 0.045f
                    ),
                    radius = grooveRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Outer purple glow
        drawCircle(
            color = PlayerPurple.copy(alpha = 0.22f),
            radius = radius - 1.dp.toPx(),
            center = center,
            style = Stroke(width = 7.dp.toPx())
        )

        // Bright neon pink-purple edge
        drawCircle(
            color = PlayerNeonPink.copy(alpha = 0.95f),
            radius = radius - 1.dp.toPx(),
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Center label
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF68636D),
                    Color(0xFF29262D)
                ),
                center = center,
                radius = radius * 0.34f
            ),
            radius = radius * 0.34f,
            center = center
        )

        // Center hole
        drawCircle(
            color = Color(0xFF08080A),
            radius = radius * 0.075f,
            center = center
        )

        // Small highlight on the disc
        drawArc(
            color = Color.White.copy(alpha = 0.13f),
            startAngle = 205f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                center.x - radius * 0.72f,
                center.y - radius * 0.72f
            ),
            size = Size(
                radius * 1.44f,
                radius * 1.44f
            ),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}


@Composable
private fun PlaybackWaveform(
    progress: Float,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(
        label = "waveformAnimation"
    )

    val animatedPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI.toFloat()),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveformPhase"
    )

    // Fixed bar heights make the waveform recognizable and stable.
    val heights = remember {
        List( thirtyBars ) { index ->
            val wave = sin(index * 0.72).toFloat()
            (0.22f + (wave + 1f) * 0.34f).coerceIn(0.15f, 0.95f)
        }
    }

    Canvas(modifier = modifier) {
        val barCount = heights.size
        val barWidth = size.width / (barCount * 1.8f)
        val gap = barWidth * 0.8f
        val totalBarWidth = barWidth + gap
        val activeBars = (progress.coerceIn(0f, 1f) * barCount).toInt()

        heights.forEachIndexed { index, baseHeight ->
            val animatedAdjustment = if (isPlaying && index < activeBars) {
                sin(animatedPhase + index * 0.45f).toFloat() * 0.12f
            } else {
                0f
            }

            val barHeight = size.height *
                (baseHeight + animatedAdjustment).coerceIn(0.12f, 1f)

            val x = index * totalBarWidth + barWidth / 2f
            val top = (size.height - barHeight) / 2f

            drawLine(
                color = if (index < activeBars) {
                    PlayerPurple
                } else {
                    PlayerWaveInactive
                },
                start = Offset(x, top),
                end = Offset(x, top + barHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

private const val thirtyBars = 30

private fun formatPlayerTime(timeMs: Long): String {
    val totalSeconds = (timeMs.coerceAtLeast(0L)) / 1000L
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(minutes, seconds)
}

private fun downloadSong(
    context: Context,
    songTitle: String,
    audioUrl: String
) {
    try {
        val uri = Uri.parse(audioUrl)

        require(
            uri.scheme == "https" || uri.scheme == "http"
        ) {
            "The song URL must be an absolute HTTP or HTTPS URL."
        }

        require(!uri.host.isNullOrBlank()) {
            "Invalid song URL."
        }

        val fileName = sanitizeFileName(songTitle) + ".mp3"

        val request = DownloadManager.Request(uri)
            .setTitle(songTitle)
            .setDescription("Downloading your song...")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        val downloadManager = context.getSystemService(
            Context.DOWNLOAD_SERVICE
        ) as DownloadManager

        downloadManager.enqueue(request)

        Toast.makeText(
            context,
            "Download started",
            Toast.LENGTH_SHORT
        ).show()

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Unable to start download: ${e.message ?: "Invalid URL"}",
            Toast.LENGTH_LONG
        ).show()
    }
}
private fun shareSong(
    context: Context,
    songTitle: String,
    artistName: String,
    audioUrl: String
) {
    try {
        val shareText = """
            Listen to "$songTitle" by $artistName on Oya!Sing 🎵

            $audioUrl
        """.trimIndent()

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, songTitle)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val chooser = Intent.createChooser(
            sendIntent,
            "Share song via"
        )

        context.startActivity(chooser)

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Unable to share this song",
            Toast.LENGTH_SHORT
        ).show()
    }
}
private fun sanitizeFileName(title: String): String {
    return title
        .replace(Regex("[\\\\/:*?\"<>|]"), "_")
        .trim()
        .ifBlank { "OyaSing_Song" }
}