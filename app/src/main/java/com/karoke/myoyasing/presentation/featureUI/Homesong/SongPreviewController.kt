package com.karoke.myoyasing.presentation.featureUI.Homesong

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.karoke.myoyasing.core.utils.recordinglogic.LyricsLine

class SongPreviewController(
    private val player: ExoPlayer
) {

    var playbackSpeed by mutableFloatStateOf(1f)
        private set

    var lyrics by mutableStateOf<List<LyricsLine>>(emptyList())
        private set

    var currentLyricIndex by mutableIntStateOf(-1)
        private set
    var currentSongId by mutableStateOf<String?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var playbackError by mutableStateOf<String?>(null)
        private set
    var playbackPosition by mutableLongStateOf(0L)
        private set

    var playbackDuration by mutableLongStateOf(0L)
        private set

    fun updateProgress() {

        playbackPosition =
            player.currentPosition.coerceAtLeast(0L)

        playbackDuration =
            player.duration
                .takeIf { it != C.TIME_UNSET }
                ?.coerceAtLeast(0L)
                ?: 0L

        currentLyricIndex =
            lyrics.indexOfLast {
                playbackPosition >= it.timeMs
            }
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs.coerceIn(0L, playbackDuration))
        updateProgress()
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        player.play()
    }
    private val playerListener = object : Player.Listener {

        override fun onIsPlayingChanged(playing: Boolean) {
            isPlaying = playing
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateProgress()

            if (playbackState == Player.STATE_ENDED) {
                isPlaying = false
                playbackPosition = playbackDuration
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            playbackError = error.message ?: "Unable to play this song"
            isPlaying = false
        }
    }

    init {
        player.addListener(playerListener)
    }

    fun togglePlayback(songId: String, audioUrl: String) {
        playbackError = null

        if (currentSongId == songId) {
            when {
                player.playbackState == Player.STATE_ENDED -> {
                    player.seekTo(0)
                    player.play()
                }

                player.isPlaying -> {
                    player.pause()
                }

                else -> {
                    player.play()
                }
            }
            return
        }

        currentSongId = songId

        player.setMediaItem(MediaItem.fromUri(audioUrl))
        player.prepare()
        player.play()
    }

    fun stop() {
        player.stop()
        player.clearMediaItems()

        currentSongId = null
        isPlaying = false
        playbackError = null
        playbackPosition = 0L
        playbackDuration = 0L
    }

    fun release() {
        player.removeListener(playerListener)
        player.release()
    }


    fun updatePlaybackSpeed(speed: Float) {

        playbackSpeed = speed

        player.playbackParameters =
            PlaybackParameters(speed)
    }
    fun updateLyrics(
        lines: List<LyricsLine>
    ) {
        lyrics = lines
    }



}

@Composable
fun rememberSongPreviewController(): SongPreviewController {
    val context = LocalContext.current

    val controller = remember {
        SongPreviewController(
            ExoPlayer.Builder(context.applicationContext).build()
        )
    }

    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    return controller

}

