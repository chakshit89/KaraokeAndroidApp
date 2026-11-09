package com.karoke.myoyasing.presentation.feed

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class FeedAudioPlayer(context: Context) {

    val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun play(url: String) {
        player.stop()
        player.clearMediaItems()

        player.setMediaItem(
            MediaItem.fromUri(url)
        )
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()
        player.playWhenReady = true
    }

    fun togglePlayback() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun pause() {
        player.pause()
    }

    fun release() {
        player.stop()
        player.release()
    }
}