package com.karoke.myoyasing.core.utils.recordinglogic

import android.Manifest
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.core.utils.KaraokeMp3Encoder
import com.karoke.myoyasing.recordinglogics.KaraokeAudioEncoder
import com.karoke.myoyasing.recordinglogics.KaraokeRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File


data class KaraokeUiState(
    val lyrics: List<LyricsLine> = emptyList(),
    val currentLyricIndex: Int = -1,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isRecording: Boolean = false,
    val isProcessing: Boolean = false,
    val recordingFile: File? = null,
    val errorMessage: String? = null,
    val micAmplitude: Float = 0f
)

class KaraokeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val recorder = KaraokeRecorder()
    private val decoder = InstrumentalAudioDecoder()
    private val mixer = KaraokeAudioMixer()

    private val _uiState = MutableStateFlow(KaraokeUiState())
    val uiState = _uiState.asStateFlow()

    private var trackingJob: Job? = null
    private var processingJob: Job? = null

    /*
     * The decoded instrumental is prepared while the song is loading/playing.
     * The mutex prevents preload and finishRecording from decoding the same
     * instrumental simultaneously.
     */
    private val instrumentalDecodeMutex = Mutex()
    private var preparedInstrumentalUrl: String? = null
    private var preparedInstrumental: DecodedAudio? = null

    fun loadLyrics(lrc: String) {
        val parsedLyrics = LrcParser.parse(lrc)

        _uiState.update {
            it.copy(
                lyrics = parsedLyrics,
                currentLyricIndex = -1
            )
        }
    }

    private fun findCurrentLyricIndex(
        lyrics: List<LyricsLine>,
        positionMs: Long
    ): Int {
        if (lyrics.isEmpty()) return -1

        if (positionMs < lyrics.first().timeMs) {
            return -1
        }

        for (index in lyrics.indices.reversed()) {
            if (positionMs >= lyrics[index].timeMs) {
                return index
            }
        }

        return -1
    }

    /**
     * Call this as soon as the selected song's instrumental URL is available.
     * It downloads and decodes the instrumental in the background so the
     * user does not have to wait for decoding after pressing Done.
     */
    fun preloadInstrumental(instrumentalUrl: String) {
        if (instrumentalUrl.isBlank()) {
            Log.w("KaraokeViewModel", "Cannot preload: instrumental URL is blank")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("KaraokePerformance", "Preloading instrumental...")

                getOrDecodeInstrumental(instrumentalUrl)

                Log.d(
                    "KaraokePerformance",
                    "Instrumental preload completed"
                )
            } catch (e: Exception) {
                Log.e(
                    "KaraokeViewModel",
                    "Instrumental preload failed; it will be retried when finishing",
                    e
                )
            }
        }
    }

    /**
     * Returns the already-decoded PCM when available.
     * If preload has not completed (or failed), it decodes the instrumental
     * here. The mutex ensures only one decode for this ViewModel at a time.
     */
    private suspend fun getOrDecodeInstrumental(
        instrumentalUrl: String
    ): DecodedAudio = instrumentalDecodeMutex.withLock {

        val cached = preparedInstrumental

        if (
            preparedInstrumentalUrl == instrumentalUrl &&
            cached != null &&
            cached.file.exists() &&
            cached.file.length() > 0L
        ) {
            Log.d(
                "KaraokePerformance",
                "Using previously decoded instrumental PCM: ${cached.file.absolutePath}"
            )
            return@withLock cached
        }

        val context = getApplication<Application>()
        val cacheFile = decodedInstrumentalCacheFile(instrumentalUrl)

        // Remove an incomplete file from an earlier failed decode.
        if (cacheFile.exists() && cacheFile.length() == 0L) {
            cacheFile.delete()
        }

        val decoded = measureStage("Instrumental download + decode") {
            withContext(Dispatchers.IO) {
                decoder.decodeFromUrl(
                    context = context,
                    url = instrumentalUrl,
                    outputFile = cacheFile
                )
            }
        }

        check(decoded.file.exists() && decoded.file.length() > 0L) {
            "Decoded instrumental PCM is empty."
        }

        preparedInstrumentalUrl = instrumentalUrl
        preparedInstrumental = decoded

        Log.d(
            "KaraokePerformance",
            "Decoded instrumental ready: ${decoded.file.absolutePath}, " +
                    "sampleRate=${decoded.sampleRate}, channels=${decoded.channels}, " +
                    "size=${decoded.file.length()} bytes"
        )

        decoded
    }

    private fun decodedInstrumentalCacheFile(url: String): File {
        val cacheKey = url.hashCode().toUInt().toString(16)
        return File(
            getApplication<Application>().cacheDir,
            "karaoke_instrumental_$cacheKey.pcm"
        )
    }

    /**
     * Delete decoded PCM only after mixing has finished. The downloaded MP3
     * cache maintained by InstrumentalAudioDecoder is not deleted here.
     */
    private suspend fun clearPreparedInstrumental(instrumentalUrl: String) {
        instrumentalDecodeMutex.withLock {
            val cacheFile = decodedInstrumentalCacheFile(instrumentalUrl)

            if (preparedInstrumentalUrl == instrumentalUrl) {
                preparedInstrumental?.file?.delete()
                preparedInstrumental = null
                preparedInstrumentalUrl = null
            }

            // Also removes a partial PCM file if decoding failed before the
            // in-memory cache fields were populated.
            cacheFile.delete()

            Log.d(
                "KaraokePerformance",
                "Decoded instrumental PCM cleaned: ${cacheFile.absolutePath}"
            )
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(file: File) {
        recorder.start(file) { amplitude ->
            updateMicAmplitude(amplitude)
        }

        _uiState.update {
            it.copy(
                isRecording = true,
                recordingFile = file,
                errorMessage = null
            )
        }
    }

    private suspend fun stopRecordingInternal(): File? {
        val file = recorder.stop()

        _uiState.update {
            it.copy(
                isRecording = false,
                recordingFile = file,
                micAmplitude = 0f
            )
        }

        return file
    }

    fun stopRecording() {
        viewModelScope.launch {
            stopRecordingInternal()
        }
    }

    fun stopRecording(onFinished: (File?) -> Unit) {
        viewModelScope.launch {
            val file = stopRecordingInternal()
            onFinished(file)
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun restartRecording(
        file: File,
        onStarted: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val oldRecording = stopRecordingInternal()

                oldRecording?.let { oldFile ->
                    val oldMelodyFile = File(
                        oldFile.parentFile,
                        "${oldFile.nameWithoutExtension}_melody.json"
                    )

                    if (oldFile.exists() && !oldFile.delete()) {
                        Log.w(
                            "KaraokeViewModel",
                            "Could not delete old recording: ${oldFile.absolutePath}"
                        )
                    }

                    if (oldMelodyFile.exists() && !oldMelodyFile.delete()) {
                        Log.w(
                            "KaraokeViewModel",
                            "Could not delete old melody: ${oldMelodyFile.absolutePath}"
                        )
                    }
                }

                file.parentFile?.mkdirs()

                if (file.exists() && !file.delete()) {
                    throw IllegalStateException(
                        "Could not clear the new recording file"
                    )
                }

                recorder.start(file) { amplitude ->
                    updateMicAmplitude(amplitude)
                }

                _uiState.update {
                    it.copy(
                        isRecording = true,
                        recordingFile = file,
                        positionMs = 0L,
                        currentLyricIndex = -1,
                        errorMessage = null
                    )
                }

                withContext(Dispatchers.Main) {
                    onStarted()
                }
            } catch (e: Exception) {
                Log.e("KaraokeViewModel", "Restart failed", e)

                _uiState.update {
                    it.copy(
                        isRecording = false,
                        errorMessage = e.message
                            ?: "Unable to restart recording"
                    )
                }
            }
        }
    }

    fun finishRecording(
        instrumentalUrl: String,
        onFinished: (File?) -> Unit
    ) {
        if (instrumentalUrl.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Instrumental audio URL is missing."
                )
            }
            onFinished(null)
            return
        }

        if (processingJob?.isActive == true) {
            Log.w("KaraokeViewModel", "finishRecording ignored: processing already active")
            return
        }

        processingJob = viewModelScope.launch(Dispatchers.IO) {
            var finalWavFile: File? = null
            var vocalFile: File? = null
            var mixedPcmFile: File? = null
            var sessionDir: File? = null

            try {
                _uiState.update {
                    it.copy(
                        isProcessing = true,
                        errorMessage = null
                    )
                }

                // 1. Stop microphone recording. The recorder also saves
                // user_vocals_melody.json.
                val recordedVocalFile = measureStage("Stop vocal recording") {
                    stopRecordingInternal()
                } ?: throw IllegalStateException(
                    "No vocal recording was created."
                )
                vocalFile = recordedVocalFile

                val outputSessionDir = recordedVocalFile.parentFile
                    ?: throw IllegalStateException(
                        "Performance directory is missing."
                    )
                sessionDir = outputSessionDir

                // Keep the final WAV and MP3 in the performance session.
                val outputWavFile = File(outputSessionDir, "final_mix.wav")
                val outputMp3File = File(outputSessionDir, "final_mix.mp3")
                val temporaryMixedPcmFile = File(
                    outputSessionDir,
                    "mixed_tmp.pcm"
                )

                finalWavFile = outputWavFile
                mixedPcmFile = temporaryMixedPcmFile

                // 2. Reuse the decoded PCM prepared during song loading.
                // If preload is still running, this waits for it instead of
                // starting a duplicate decode.
                val decoded = getOrDecodeInstrumental(instrumentalUrl)

                // 3. Mix decoded instrumental PCM with the recorded vocals.
                measureStage("Mix instrumental + vocals") {
                    mixer.mix(
                        instrumental = decoded,
                        vocalFile = recordedVocalFile,
                        outputFile = temporaryMixedPcmFile
                    )
                }

                check(
                    temporaryMixedPcmFile.exists() &&
                            temporaryMixedPcmFile.length() > 0L
                ) {
                    "Mixed PCM file was not created."
                }

                // 4. Convert only the final mix to WAV.
                // The separate instrumental.wav conversion was removed:
                // KaraokeAudioMixer reads decoded.file directly.
                measureStage("Encode final WAV") {
                    KaraokeAudioEncoder.encodeWav(
                        pcmFile = temporaryMixedPcmFile,
                        outputFile = outputWavFile,
                        sampleRate = KaraokeAudioMixer.OUTPUT_SAMPLE_RATE,
                        channels = KaraokeAudioMixer.OUTPUT_CHANNELS,
                        bitsPerSample = KaraokeAudioMixer.OUTPUT_BITS
                    )
                }

                check(outputWavFile.exists() && outputWavFile.length() > 44L) {
                    "Final mixed recording was not created correctly."
                }

                // 5. Convert final WAV to MP3 for uploading.
                // Keep the existing encoder API and final_mix.wav file.
                measureStage("Encode final MP3") {
                    KaraokeMp3Encoder.encodeWavToMp3(
                        wavFile = outputWavFile,
                        mp3File = outputMp3File
                    )
                }

                check(outputMp3File.exists() && outputMp3File.length() > 0L) {
                    "Final MP3 recording was not created correctly."
                }

                Log.d(
                    "KaraokeViewModel",
                    "MP3 ready for upload: ${outputMp3File.absolutePath}, " +
                            "size=${outputMp3File.length()} bytes"
                )

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        recordingFile = outputWavFile,
                        errorMessage = null
                    )
                }

                val melodyFile = File(
                    recordedVocalFile.parentFile,
                    "${recordedVocalFile.nameWithoutExtension}_melody.json"
                )

                Log.d(
                    "KaraokeViewModel",
                    """
                    Performance files created:
                    Vocal: ${recordedVocalFile.absolutePath}
                    Melody: ${melodyFile.absolutePath}
                    Final WAV: ${outputWavFile.absolutePath}
                    Final MP3: ${outputMp3File.absolutePath}
                    """.trimIndent()
                )
            } catch (e: Exception) {
                Log.e(
                    "KaraokeViewModel",
                    "Failed to finish recording",
                    e
                )

                finalWavFile = null

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = e.message
                            ?: "Unable to create performance."
                    )
                }
            } finally {
                // The mixer has finished (successfully or with an exception),
                // so temporary mixed PCM and decoded instrumental PCM can
                // now be removed. Keep the vocal WAV and melody JSON.
                mixedPcmFile?.delete()

                try {
                    clearPreparedInstrumental(instrumentalUrl)
                } catch (cleanupError: Exception) {
                    Log.w(
                        "KaraokeViewModel",
                        "Could not clean decoded instrumental cache",
                        cleanupError
                    )
                }

                // Do not delete vocalFile:
                // user_vocals.wav and user_vocals_melody.json are retained.
            }

            try {
                withContext(Dispatchers.Main) {
                    onFinished(finalWavFile)
                }
            } finally {
                processingJob = null
            }
        }
    }

    fun updatePlayback(
        positionMs: Long,
        durationMs: Long
    ) {
        val lyrics = _uiState.value.lyrics

        val currentIndex = findCurrentLyricIndex(
            lyrics,
            positionMs
        )

        _uiState.update {
            it.copy(
                positionMs = positionMs,
                durationMs = durationMs,
                currentLyricIndex = currentIndex
            )
        }
    }

    fun startTracking(
        getPosition: () -> Long,
        getDuration: () -> Long
    ) {
        trackingJob?.cancel()

        trackingJob = viewModelScope.launch {
            while (isActive) {
                updatePlayback(
                    positionMs = getPosition(),
                    durationMs = getDuration()
                )

                delay(30)
            }
        }
    }

    fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
    }

    override fun onCleared() {
        trackingJob?.cancel()
        processingJob?.cancel()
        recorder.release()
        super.onCleared()
    }

    private fun updateMicAmplitude(amplitude: Float) {
        _uiState.update {
            it.copy(micAmplitude = amplitude)
        }
    }

    private suspend fun <T> measureStage(
        name: String,
        block: suspend () -> T
    ): T {
        val start = android.os.SystemClock.elapsedRealtime()

        return try {
            block()
        } finally {
            Log.d(
                "KaraokePerformance",
                "$name took ${
                    android.os.SystemClock.elapsedRealtime() - start
                } ms"
            )
        }
    }
}
