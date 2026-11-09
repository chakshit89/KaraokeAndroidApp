package com.karoke.myoyasing.core.utils.recordinglogic

import android.content.Context
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import androidx.annotation.RawRes
import com.karoke.myoyasing.di.network.OkHttpProvider
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class DecodedAudio(
    val file: File,
    val sampleRate: Int,
    val channels: Int,
    val bitsPerSample: Int = 16
)

class InstrumentalAudioDecoder {

    fun decodeFromUrl(
        context: Context,
        url: String,
        outputFile: File
    ): DecodedAudio {
        require(url.isNotBlank()) {
            "Instrumental audio URL is empty."
        }

        outputFile.parentFile?.mkdirs()

        val cachedMp3 = File(
            context.cacheDir,
            "instrumental_${url.hashCode().toUInt().toString(16)}.mp3"
        )

        if (!cachedMp3.exists() || cachedMp3.length() == 0L) {
            val temporaryMp3 = File.createTempFile(
                "instrumental_download_",
                ".mp3",
                context.cacheDir
            )

            try {
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                OkHttpProvider.client.newCall(request).execute().use { response ->
                    check(response.isSuccessful) {
                        "Unable to download instrumental audio: HTTP ${response.code}"
                    }

                    val body = response.body
                        ?: error("Instrumental audio response is empty.")

                    temporaryMp3.outputStream().use { output ->
                        body.byteStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                }

                check(temporaryMp3.length() > 0L) {
                    "Downloaded instrumental audio is empty."
                }

                if (!temporaryMp3.renameTo(cachedMp3)) {
                    temporaryMp3.copyTo(cachedMp3, overwrite = true)
                }
            } finally {
                temporaryMp3.delete()
            }
        }

        return decodeMp3(
            inputFile = cachedMp3,
            outputFile = outputFile
        )
    }

    private fun decodeMp3(
        inputFile: File,
        outputFile: File
    ): DecodedAudio {
        val extractor = MediaExtractor()
        var codec: MediaCodec? = null

        try {
            extractor.setDataSource(inputFile.absolutePath)

            var audioTrackIndex = -1
            var audioFormat: MediaFormat? = null

            for (index in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(index)
                val mime = format.getString(MediaFormat.KEY_MIME)

                if (mime != null && mime.startsWith("audio/")) {
                    audioTrackIndex = index
                    audioFormat = format
                    break
                }
            }

            check(audioTrackIndex >= 0) {
                "No audio track found in instrumental audio."
            }

            val format = audioFormat
                ?: error("Audio format is unavailable.")

            val mime = format.getString(MediaFormat.KEY_MIME)
                ?: error("Audio MIME type is unavailable.")

            val inputSampleRate = format.getInteger(
                MediaFormat.KEY_SAMPLE_RATE
            )

            val inputChannels = format.getInteger(
                MediaFormat.KEY_CHANNEL_COUNT
            )

            extractor.selectTrack(audioTrackIndex)

            codec = MediaCodec.createDecoderByType(mime)
            codec.configure(format, null, null, 0)
            codec.start()

            outputFile.parentFile?.mkdirs()

            outputFile.outputStream().use { output ->
                val bufferInfo = MediaCodec.BufferInfo()

                var inputDone = false
                var outputDone = false
                var actualSampleRate = inputSampleRate
                var actualChannels = inputChannels

                while (!outputDone) {
                    if (!inputDone) {
                        val inputIndex = codec.dequeueInputBuffer(10_000)

                        if (inputIndex >= 0) {
                            val inputBuffer = codec.getInputBuffer(inputIndex)

                            if (inputBuffer != null) {
                                inputBuffer.clear()

                                val sampleSize = extractor.readSampleData(
                                    inputBuffer,
                                    0
                                )

                                if (sampleSize < 0) {
                                    codec.queueInputBuffer(
                                        inputIndex,
                                        0,
                                        0,
                                        0,
                                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                    )
                                    inputDone = true
                                } else {
                                    val sampleTime = extractor.sampleTime

                                    codec.queueInputBuffer(
                                        inputIndex,
                                        0,
                                        sampleSize,
                                        sampleTime,
                                        0
                                    )

                                    extractor.advance()
                                }
                            } else {
                                codec.queueInputBuffer(
                                    inputIndex,
                                    0,
                                    0,
                                    0,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                )
                                inputDone = true
                            }
                        }
                    }

                    when (
                        val outputIndex = codec.dequeueOutputBuffer(
                            bufferInfo,
                            10_000
                        )
                    ) {
                        MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                            val newFormat = codec.outputFormat

                            if (newFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
                                actualSampleRate = newFormat.getInteger(
                                    MediaFormat.KEY_SAMPLE_RATE
                                )
                            }

                            if (newFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
                                actualChannels = newFormat.getInteger(
                                    MediaFormat.KEY_CHANNEL_COUNT
                                )
                            }
                        }

                        MediaCodec.INFO_TRY_AGAIN_LATER -> Unit

                        else -> {
                            if (outputIndex >= 0) {
                                val outputBuffer = codec.getOutputBuffer(outputIndex)

                                if (outputBuffer != null && bufferInfo.size > 0) {
                                    outputBuffer.position(bufferInfo.offset)
                                    outputBuffer.limit(
                                        bufferInfo.offset + bufferInfo.size
                                    )

                                    val pcm = convertDecoderOutputToPcm16(
                                        outputBuffer,
                                        codec.outputFormat
                                    )

                                    output.write(pcm)
                                }

                                codec.releaseOutputBuffer(outputIndex, false)

                                if (
                                    bufferInfo.flags and
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                                ) {
                                    outputDone = true
                                }
                            }
                        }
                    }
                }

                check(outputFile.exists() && outputFile.length() > 0L) {
                    "Instrumental audio decoding produced an empty PCM file."
                }

                return DecodedAudio(
                    file = outputFile,
                    sampleRate = actualSampleRate,
                    channels = actualChannels,
                    bitsPerSample = 16
                )
            }
        } finally {
            try {
                codec?.stop()
            } catch (_: Exception) {
            }

            try {
                codec?.release()
            } catch (_: Exception) {
            }

            extractor.release()
        }
    }

    private fun convertDecoderOutputToPcm16(
        buffer: ByteBuffer,
        format: MediaFormat
    ): ByteArray {
        val encoding = if (format.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
            format.getInteger(MediaFormat.KEY_PCM_ENCODING)
        } else {
            AudioFormat.ENCODING_PCM_16BIT
        }

        return when (encoding) {
            AudioFormat.ENCODING_PCM_16BIT -> {
                ByteArray(buffer.remaining()).also { result ->
                    buffer.get(result)
                }
            }

            AudioFormat.ENCODING_PCM_FLOAT -> {
                val input = buffer.slice().order(ByteOrder.LITTLE_ENDIAN)
                val sampleCount = input.remaining() / 4
                val result = ByteArray(sampleCount * 2)
                var outputIndex = 0

                repeat(sampleCount) {
                    val floatSample = input.float
                    val clamped = floatSample.coerceIn(-1f, 1f)
                    val shortSample = (clamped * 32767f)
                        .toInt()
                        .coerceIn(-32768, 32767)

                    result[outputIndex++] = (shortSample and 0xFF).toByte()
                    result[outputIndex++] = (shortSample shr 8).toByte()
                }

                result
            }

            else -> {
                throw IllegalStateException(
                    "Unsupported decoder PCM encoding: $encoding"
                )
            }
        }
    }
}