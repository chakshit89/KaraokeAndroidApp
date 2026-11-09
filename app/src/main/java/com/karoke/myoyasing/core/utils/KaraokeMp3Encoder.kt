package com.karoke.myoyasing.core.utils


import com.naman14.androidlame.AndroidLame
import com.naman14.androidlame.LameBuilder
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream

object KaraokeMp3Encoder {

    private const val SAMPLE_RATE = 44_100
    private const val CHANNELS = 2
    private const val BYTES_PER_SAMPLE = 2
    private const val WAV_HEADER_SIZE = 44
    private const val FRAMES_PER_CHUNK = 4096

    fun encodeWavToMp3(
        wavFile: File,
        mp3File: File
    ): File {

        require(wavFile.exists() && wavFile.length() > WAV_HEADER_SIZE) {
            "Input WAV file does not exist or is empty."
        }

        mp3File.parentFile?.mkdirs()

        val encoder: AndroidLame = LameBuilder()
            .setInSampleRate(SAMPLE_RATE)
            .setOutSampleRate(SAMPLE_RATE)
            .setOutChannels(CHANNELS)
            .setOutBitrate(192)
            .setQuality(2)
            .build()

        val left = ShortArray(FRAMES_PER_CHUNK)
        val right = ShortArray(FRAMES_PER_CHUNK)

        val pcmBuffer = ByteArray(
            FRAMES_PER_CHUNK * CHANNELS * BYTES_PER_SAMPLE
        )

        val mp3Buffer = ByteArray(
            7200 + (pcmBuffer.size * 1.25).toInt()
        )

        try {
            BufferedInputStream(
                FileInputStream(wavFile),
                64 * 1024
            ).use { input ->

                // Skip the 44-byte PCM WAV header.
                var skipped = 0L

                while (skipped < WAV_HEADER_SIZE) {
                    val count = input.skip(
                        WAV_HEADER_SIZE - skipped.toLong()
                    )

                    if (count <= 0L) {
                        if (input.read() == -1) {
                            error("Unable to read WAV header.")
                        }
                        skipped++
                    } else {
                        skipped += count
                    }
                }

                val remainingPcmBytes =
                    wavFile.length() - WAV_HEADER_SIZE

                require(
                    remainingPcmBytes > 0 &&
                        remainingPcmBytes %
                        (CHANNELS * BYTES_PER_SAMPLE) == 0L
                ) {
                    "Invalid stereo PCM WAV data."
                }

                var remainingBytes = remainingPcmBytes

                BufferedOutputStream(
                    mp3File.outputStream(),
                    64 * 1024
                ).use { output ->

                    while (remainingBytes > 0) {

                        val bytesToRead = minOf(
                            pcmBuffer.size.toLong(),
                            remainingBytes
                        ).toInt()

                        var offset = 0

                        while (offset < bytesToRead) {
                            val count = input.read(
                                pcmBuffer,
                                offset,
                                bytesToRead - offset
                            )

                            if (count < 0) {
                                error("Unexpected end of WAV file.")
                            }

                            offset += count
                        }

                        val frames =
                            bytesToRead / (CHANNELS * BYTES_PER_SAMPLE)

                        // Convert interleaved stereo PCM to left/right arrays.
                        for (i in 0 until frames) {
                            val byteIndex = i * 4

                            left[i] = (
                                (pcmBuffer[byteIndex + 1].toInt() shl 8) or
                                    (pcmBuffer[byteIndex].toInt() and 0xFF)
                                ).toShort()

                            right[i] = (
                                (pcmBuffer[byteIndex + 3].toInt() shl 8) or
                                    (pcmBuffer[byteIndex + 2].toInt() and 0xFF)
                                ).toShort()
                        }

                        val encodedBytes = encoder.encode(
                            left,
                            right,
                            frames,
                            mp3Buffer
                        )

                        if (encodedBytes > 0) {
                            output.write(
                                mp3Buffer,
                                0,
                                encodedBytes
                            )
                        }

                        remainingBytes -= bytesToRead
                    }

                    val flushedBytes = encoder.flush(mp3Buffer)

                    if (flushedBytes > 0) {
                        output.write(
                            mp3Buffer,
                            0,
                            flushedBytes
                        )
                    }
                }
            }

            check(mp3File.exists() && mp3File.length() > 0L) {
                "MP3 encoding failed: output file is empty."
            }

            return mp3File

        } catch (e: Exception) {
            mp3File.delete()
            throw e
        }
    }
}