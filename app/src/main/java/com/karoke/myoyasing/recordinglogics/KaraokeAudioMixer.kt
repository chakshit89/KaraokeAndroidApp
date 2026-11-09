package com.karoke.myoyasing.core.utils.recordinglogic

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

class KaraokeAudioMixer {

    companion object {
        const val OUTPUT_SAMPLE_RATE = 44_100
        const val OUTPUT_CHANNELS = 2
        const val OUTPUT_BITS = 16

        const val DEFAULT_VOCAL_LATENCY_MS = 60L

        private const val INSTRUMENTAL_GAIN = 0.55f
        private const val VOCAL_GAIN = 1.50f
    }

    fun mix(
        instrumental: DecodedAudio,
        vocalFile: File,
        outputFile: File,
        vocalLatencyMs: Long = DEFAULT_VOCAL_LATENCY_MS
    ): File {

        require(instrumental.file.exists()) {
            "Instrumental PCM file does not exist: ${instrumental.file.absolutePath}"
        }

        require(vocalFile.exists()) {
            "Vocal recording does not exist: ${vocalFile.absolutePath}"
        }

        outputFile.parentFile?.mkdirs()

        val instrumentalSamples = readInstrumental(
            file = instrumental.file,
            sampleRate = instrumental.sampleRate,
            channels = instrumental.channels
        )

        val vocalSamples = normalizeVocal(readVocal(vocalFile))

        val latencySamples =
            ((vocalLatencyMs * OUTPUT_SAMPLE_RATE) / 1000L)
                .toInt()
                .coerceAtLeast(0)

        val usableVocalSamples =
            (vocalSamples.size - latencySamples)
                .coerceAtLeast(0)

        val vocalFrames =
            usableVocalSamples

        val instrumentalFrames =
            instrumentalSamples.size / OUTPUT_CHANNELS

        val totalFrames =
            minOf(
                instrumentalFrames,
                vocalFrames
            )

        val mixedSamples =
            ShortArray(totalFrames * OUTPUT_CHANNELS)


        for (frame in 0 until totalFrames) {
            val instrumentalIndex = frame * OUTPUT_CHANNELS
            val vocalIndex = frame + latencySamples

            val instrumentalLeft =
                if (instrumentalIndex < instrumentalSamples.size) {
                    instrumentalSamples[instrumentalIndex] / 32768f
                } else {
                    0f
                }

            val instrumentalRight =
                if (instrumentalIndex + 1 < instrumentalSamples.size) {
                    instrumentalSamples[instrumentalIndex + 1] / 32768f
                } else {
                    0f
                }

            val vocal =
                if (vocalIndex < vocalSamples.size) {
                    vocalSamples[vocalIndex] / 32768f
                } else {
                    0f
                }

            // Independently adjust music and microphone levels.
            val left =
                instrumentalLeft * INSTRUMENTAL_GAIN +
                        vocal * VOCAL_GAIN

            val right =
                instrumentalRight * INSTRUMENTAL_GAIN +
                        vocal * VOCAL_GAIN

            val limitedLeft = kotlin.math.tanh(left.toDouble()).toFloat()
            val limitedRight = kotlin.math.tanh(right.toDouble()).toFloat()

            mixedSamples[instrumentalIndex] =
                (limitedLeft * 32767f)
                    .roundToInt()
                    .coerceIn(-32768, 32767)
                    .toShort()

            mixedSamples[instrumentalIndex + 1] =
                (limitedRight * 32767f)
                    .roundToInt()
                    .coerceIn(-32768, 32767)
                    .toShort()
        }


        writePcm(
            samples = mixedSamples,
            outputFile = outputFile
        )

        return outputFile
    }

    private fun readVocal(
        file: File
    ): ShortArray {

        require(file.length() >= 44) {
            "Invalid vocal WAV file"
        }

        BufferedInputStream(
            FileInputStream(file),
            64 * 1024
        ).use { input ->

            input.skip(44)

            val pcmBytes =
                (file.length() - 44)
                    .coerceAtLeast(0L)
                    .coerceAtMost(Int.MAX_VALUE.toLong())
                    .toInt()

            val bytes =
                ByteArray(pcmBytes)

            var offset = 0

            while (offset < bytes.size) {

                val read =
                    input.read(
                        bytes,
                        offset,
                        bytes.size - offset
                    )

                if (read < 0) break

                offset += read
            }

            val sampleCount =
                offset / 2

            val samples =
                ShortArray(sampleCount)

            val buffer =
                ByteBuffer
                    .wrap(bytes, 0, offset)
                    .order(ByteOrder.LITTLE_ENDIAN)

            for (i in 0 until sampleCount) {
                samples[i] = buffer.short
            }

            return samples
        }
    }

    private fun readInstrumental(
        file: File,
        sampleRate: Int,
        channels: Int
    ): ShortArray {

        require(sampleRate > 0) {
            "Invalid instrumental sample rate: $sampleRate"
        }

        require(channels > 0) {
            "Invalid instrumental channel count: $channels"
        }

        val bytes =
            file.length()
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()

        val input =
            ByteArray(bytes)

        FileInputStream(file).use { stream ->

            var offset = 0

            while (offset < input.size) {

                val read =
                    stream.read(
                        input,
                        offset,
                        input.size - offset
                    )

                if (read < 0) break

                offset += read
            }

            return resampleInstrumental(
                data = input,
                bytesRead = offset,
                sourceSampleRate = sampleRate,
                sourceChannels = channels
            )
        }
    }

    private fun resampleInstrumental(
        data: ByteArray,
        bytesRead: Int,
        sourceSampleRate: Int,
        sourceChannels: Int
    ): ShortArray {

        val sourceBytesPerFrame =
            sourceChannels * 2

        if (bytesRead < sourceBytesPerFrame) {
            return ShortArray(0)
        }

        val sourceFrames =
            bytesRead / sourceBytesPerFrame

        val targetFrames =
            if (sourceSampleRate == OUTPUT_SAMPLE_RATE) {
                sourceFrames
            } else {
                (
                        sourceFrames.toDouble() *
                                OUTPUT_SAMPLE_RATE /
                                sourceSampleRate
                        ).roundToInt()
            }

        val output =
            ShortArray(
                targetFrames * OUTPUT_CHANNELS
            )

        val buffer =
            ByteBuffer
                .wrap(data, 0, bytesRead)
                .order(ByteOrder.LITTLE_ENDIAN)

        val source =
            ShortArray(
                sourceFrames * sourceChannels
            )

        for (i in source.indices) {
            source[i] = buffer.short
        }

        for (targetFrame in 0 until targetFrames) {

            val sourcePosition =
                targetFrame.toDouble() *
                        sourceSampleRate /
                        OUTPUT_SAMPLE_RATE

            val sourceIndex =
                sourcePosition.toInt()

            val nextIndex =
                (sourceIndex + 1)
                    .coerceAtMost(sourceFrames - 1)

            val fraction =
                (sourcePosition - sourceIndex)
                    .toFloat()
                    .coerceIn(0f, 1f)

            val left1 =
                getSourceChannel(
                    source = source,
                    frame = sourceIndex,
                    channel = 0,
                    channels = sourceChannels
                )

            val left2 =
                getSourceChannel(
                    source = source,
                    frame = nextIndex,
                    channel = 0,
                    channels = sourceChannels
                )

            val right1 =
                getSourceChannel(
                    source = source,
                    frame = sourceIndex,
                    channel = 1,
                    channels = sourceChannels
                )

            val right2 =
                getSourceChannel(
                    source = source,
                    frame = nextIndex,
                    channel = 1,
                    channels = sourceChannels
                )

            val left =
                left1 + (left2 - left1) * fraction

            val right =
                right1 + (right2 - right1) * fraction

            val outputIndex =
                targetFrame * OUTPUT_CHANNELS

            output[outputIndex] =
                (left * 32767f)
                    .roundToInt()
                    .coerceIn(-32768, 32767)
                    .toShort()

            output[outputIndex + 1] =
                (right * 32767f)
                    .roundToInt()
                    .coerceIn(-32768, 32767)
                    .toShort()
        }

        return output
    }

    private fun getSourceChannel(
        source: ShortArray,
        frame: Int,
        channel: Int,
        channels: Int
    ): Float {

        val safeFrame =
            frame.coerceAtLeast(0)

        val index =
            safeFrame * channels +
                    channel.coerceAtMost(channels - 1)

        if (index >= source.size) {
            return 0f
        }

        return source[index] / 32768f
    }

    private fun writePcm(
        samples: ShortArray,
        outputFile: File
    ) {

        BufferedOutputStream(
            outputFile.outputStream(),
            64 * 1024
        ).use { output ->

            val buffer =
                ByteBuffer
                    .allocate(64 * 1024)
                    .order(ByteOrder.LITTLE_ENDIAN)

            for (sample in samples) {

                if (buffer.remaining() < 2) {

                    output.write(
                        buffer.array(),
                        0,
                        buffer.position()
                    )

                    buffer.clear()
                }

                buffer.putShort(sample)
            }

            if (buffer.position() > 0) {

                output.write(
                    buffer.array(),
                    0,
                    buffer.position()
                )
            }
        }
    }


    private fun normalizeVocal(samples: ShortArray): ShortArray {
        if (samples.isEmpty()) return samples


        var sumSquares = 0.0
        var peak = 0

        for (sample in samples) {
            val value = sample.toDouble() / 32768.0
            sumSquares += value * value
            peak = maxOf(peak, kotlin.math.abs(sample.toInt()))
        }

        val rms = kotlin.math.sqrt(sumSquares / samples.size)

        if (rms < 0.003 || peak < 100) return samples

        val targetRms = 0.14
        val gain = (targetRms / rms).coerceIn(1.0, 4.0)

        val peakGainLimit = 0.90 * 32767.0 / peak
        val safeGain = minOf(gain, peakGainLimit)

        return ShortArray(samples.size) { index ->
            (samples[index] * safeGain)
                .toInt()
                .coerceIn(-32768, 32767)
                .toShort()
        }
    }

}