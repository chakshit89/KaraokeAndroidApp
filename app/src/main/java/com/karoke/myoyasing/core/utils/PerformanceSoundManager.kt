package com.karoke.myoyasing.core.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.karoke.myoyasing.R

class PerformanceSoundManager(context: Context) {

    companion object {
        private const val TAG = "PerformanceSound"
    }
    private val appContext = context.applicationContext
    private val soundPool: SoundPool

    private val sounds = mutableMapOf<Int, Int>()
    private val loadedSounds = mutableSetOf<Int>()

    private var pendingResourceId: Int? = null
    private var released = false

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->

            if (released) return@setOnLoadCompleteListener

            if (status == 0) {
                loadedSounds.add(sampleId)

                Log.d(TAG, "Loaded successfully: $sampleId")

                if (pendingResourceId != null) {
                    val resourceId = pendingResourceId!!
                    val pendingSample = sounds[resourceId]

                    if (pendingSample == sampleId) {
                        pendingResourceId = null
                        playSample(sampleId)
                    }
                }

            } else {
                Log.e(TAG, "Loading failed: $sampleId status=$status")
            }
        }

        load(R.raw.epic_victory_s)
        load(R.raw.celebration_success)
        load(R.raw.average)
        load(R.raw.poor)
    }

    private fun load(resourceId: Int) {
        val sampleId = soundPool.load(
            appContext,
            resourceId,
            1
        )

        sounds[resourceId] = sampleId

        Log.d(TAG, "Requested loading: $resourceId sample=$sampleId")
    }

    fun playForGrade(grade: String?) {

        if (released) return

        val resourceId = when (grade?.trim()?.uppercase()) {
            "S" -> R.raw.epic_victory_s
            "A+", "A" -> R.raw.celebration_success
            "B" -> R.raw.average
            "C", "D", "E" -> R.raw.poor
            else -> {
                Log.e(TAG, "Unknown grade: $grade")
                return
            }
        }

        Log.d(TAG, "Playing requested grade: $grade")

        val sampleId = sounds[resourceId]

        if (sampleId == null || sampleId == 0) {
            Log.e(TAG, "Invalid sample ID for resource: $resourceId")
            return
        }

        if (loadedSounds.contains(sampleId)) {
            playSample(sampleId)
        } else {
            Log.d(TAG, "Sound not loaded yet. Queuing playback.")
            pendingResourceId = resourceId
        }
    }

    private fun playSample(sampleId: Int) {

        if (released) return

        val streamId = soundPool.play(
            sampleId,
            1f,
            1f,
            1,
            0,
            1f
        )

        Log.d(TAG, "Playback stream ID: $streamId")

        if (streamId == 0) {
            Log.e(TAG, "SoundPool could not start playback")
        }
    }

    fun release() {
        if (released) return

        released = true
        pendingResourceId = null

        soundPool.setOnLoadCompleteListener(null)
        soundPool.release()

        sounds.clear()
        loadedSounds.clear()
    }
}