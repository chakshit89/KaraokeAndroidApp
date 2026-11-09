package com.karoke.myoyasing.recordinglogics

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.sqrt

class KaraokeRecorder {

    companion object {
        const val SAMPLE_RATE = 44_100
        const val CHANNEL_COUNT = 1
        const val BITS_PER_SAMPLE = 16

        private const val CHANNEL_CONFIG =
            AudioFormat.CHANNEL_IN_MONO

        private const val AUDIO_FORMAT =
            AudioFormat.ENCODING_PCM_16BIT
    }

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var melodyExtractor: TarsosMelodyExtractor? = null
    private var melodyOutputFile: File? = null
    @Volatile
    private var isRecording = false

    private var outputFile: File? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun start(file: File, onAmplitude: (Float) -> Unit = {}) {
        stopInternal()

        file.parentFile?.mkdirs()

        melodyOutputFile = File(
            file.parentFile ?: throw IllegalStateException(
                "Recording file must have a parent directory"
            ),
            "${file.nameWithoutExtension}_melody.json"
        )

        melodyExtractor = TarsosMelodyExtractor(
            sampleRate = SAMPLE_RATE,
            windowSize = 1024
        )
        Log.d(
            "KaraokeRecorder",
            "Recording file: ${file.absolutePath}"
        )

        Log.d(
            "KaraokeRecorder",
            "Melody JSON will be saved to: ${melodyOutputFile?.absolutePath}"
        )
        stopInternal()

        file.parentFile?.mkdirs()

        outputFile = file

        val minBufferSize =
            AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT
            )

        require(
            minBufferSize > 0
        ) {
            "Unable to determine AudioRecord buffer size"
        }

        val bufferSize =
            maxOf(
                minBufferSize * 2,
                SAMPLE_RATE / 5
            )

        val recorder =
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

        check(
            recorder.state ==
                AudioRecord.STATE_INITIALIZED
        ) {
            recorder.release()
            "AudioRecord initialization failed"
        }

        audioRecord = recorder

        writeWavHeader(
            file = file,
            sampleRate = SAMPLE_RATE,
            channels = CHANNEL_COUNT,
            bitsPerSample = BITS_PER_SAMPLE,
            dataSize = 0
        )

        isRecording = true

        recorder.startRecording()

        recordingJob =
            CoroutineScope(
                Dispatchers.IO
            ).launch {

                val buffer =
                    ByteArray(
                        bufferSize
                    )

                RandomAccessFile(
                    file,
                    "rw"
                ).use { randomAccessFile ->

                    randomAccessFile.seek(
                        44
                    )

                    while (
                        isActive &&
                        isRecording
                    ) {

                        val read =
                            recorder.read(
                                buffer,
                                0,
                                buffer.size,
                                AudioRecord.READ_BLOCKING
                            )

                        if (read > 0) {
                            randomAccessFile.write(
                                buffer,
                                0,
                                read
                            )
                            melodyExtractor?.acceptPcm(
                                bytes = buffer,
                                byteCount = read
                            )

                            val amplitude = calculateRms(
                                buffer = buffer,
                                byteCount = read
                            )

                            onAmplitude(amplitude)

                        }
                    }
                }
            }
    }

    suspend fun stop(): File? {

        isRecording = false

        val recorder =
            audioRecord

        audioRecord = null

        try {
            if (
                recorder != null &&
                recorder.recordingState ==
                AudioRecord.RECORDSTATE_RECORDING
            ) {
                recorder.stop()
            }
        } catch (_: Exception) {
        }

        recordingJob?.join()
        recordingJob = null

        Log.d("KaraokeRecorder", "Recording job finished")

        val jsonFile = melodyOutputFile
        val extractor = melodyExtractor

        Log.d(
            "KaraokeRecorder",
            "Melody output path: ${jsonFile?.absolutePath}"
        )

        if (jsonFile != null && extractor != null) {
            try {
                Log.d("KaraokeRecorder", "Saving melody JSON...")

                extractor.save(jsonFile)

                Log.d(
                    "KaraokeRecorder",
                    "Melody JSON saved=${jsonFile.exists()}, " +
                            "size=${jsonFile.length()} bytes, " +
                            "path=${jsonFile.absolutePath}"
                )
            } catch (e: Exception) {
                Log.e(
                    "KaraokeRecorder",
                    "Failed to save melody JSON",
                    e
                )
            }
        } else {
            Log.e(
                "KaraokeRecorder",
                "Cannot save melody JSON: " +
                        "jsonFile=$jsonFile, extractor=$extractor"
            )
        }

        melodyExtractor = null
        melodyOutputFile = null

        try {
            recorder?.release()
        } catch (_: Exception) {
        }

        val file =
            outputFile

        outputFile = null

        if (
            file == null ||
            !file.exists() ||
            file.length() <= 44
        ) {
            return null
        }

        finalizeWavHeader(file)

        return file
    }

    fun release() {

        isRecording = false

        try {
            audioRecord?.stop()
        } catch (_: Exception) {
        }

        try {
            audioRecord?.release()
        } catch (_: Exception) {
        }

        audioRecord = null

        recordingJob?.cancel()
        recordingJob = null

        outputFile = null
    }

    private fun stopInternal() {

        isRecording = false

        try {
            audioRecord?.stop()
        } catch (_: Exception) {
        }

        try {
            audioRecord?.release()
        } catch (_: Exception) {
        }

        audioRecord = null

        recordingJob?.cancel()
        recordingJob = null

        outputFile = null
    }

    private fun writeWavHeader(
        file: File,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int,
        dataSize: Long
    ) {

        RandomAccessFile(
            file,
            "rw"
        ).use { raf ->

            raf.seek(0)

            raf.writeBytes("RIFF")
            writeIntLE(
                raf,
                (36 + dataSize).toInt()
            )

            raf.writeBytes("WAVE")

            raf.writeBytes("fmt ")
            writeIntLE(
                raf,
                16
            )

            writeShortLE(
                raf,
                1
            )

            writeShortLE(
                raf,
                channels
            )

            writeIntLE(
                raf,
                sampleRate
            )

            val byteRate =
                sampleRate *
                        channels *
                        bitsPerSample /
                        8

            writeIntLE(
                raf,
                byteRate
            )

            val blockAlign =
                channels *
                        bitsPerSample /
                        8

            writeShortLE(
                raf,
                blockAlign
            )

            writeShortLE(
                raf,
                bitsPerSample
            )

            raf.writeBytes("data")

            writeIntLE(
                raf,
                dataSize.toInt()
            )
        }
    }

    private fun finalizeWavHeader(
        file: File
    ) {

        val dataSize =
            file.length() - 44

        writeWavHeader(
            file = file,
            sampleRate = SAMPLE_RATE,
            channels = CHANNEL_COUNT,
            bitsPerSample = BITS_PER_SAMPLE,
            dataSize = dataSize
        )
    }

    private fun writeIntLE(
        raf: RandomAccessFile,
        value: Int
    ) {

        raf.write(
            value and 0xFF
        )

        raf.write(
            (value shr 8) and 0xFF
        )

        raf.write(
            (value shr 16) and 0xFF
        )

        raf.write(
            (value shr 24) and 0xFF
        )
    }

    private fun writeShortLE(
        raf: RandomAccessFile,
        value: Int
    ) {

        raf.write(
            value and 0xFF
        )

        raf.write(
            (value shr 8) and 0xFF
        )
    }

    private fun calculateRms(
        buffer: ByteArray,
        byteCount: Int
    ): Float {
        var sumSquares = 0.0
        var sampleCount = 0
        var index = 0

        while (index + 1 < byteCount) {
            val sample = (
                    (buffer[index + 1].toInt() shl 8) or
                            (buffer[index].toInt() and 0xFF)
                    ).toShort().toInt()

            sumSquares += sample.toDouble() * sample
            sampleCount++
            index += 2
        }

        if (sampleCount == 0) return 0f

        return (
                sqrt(sumSquares / sampleCount) / 32768.0
                ).toFloat().coerceIn(0f, 1f)
    }
}