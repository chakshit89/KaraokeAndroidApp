package com.karoke.myoyasing.recordinglogics.cleanAudio

import android.content.Context
import com.rikorose.deepfilternet.NativeDeepFilterNet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DeepFilterNetProcessor(
    private val context: Context
) {

    companion object {
        private const val SAMPLE_RATE = 48_000
        private const val CHANNELS = 1
        private const val BITS_PER_SAMPLE = 16
        private const val WAV_HEADER_SIZE = 44
    }

    suspend fun cleanWav(
        inputFile: File,
        outputFile: File,
        attenuationDb: Float = 30f
    ): File = withContext(Dispatchers.IO) {

        require(inputFile.exists()) {
            "Input WAV file does not exist: ${inputFile.absolutePath}"
        }

        require(inputFile.length() > WAV_HEADER_SIZE) {
            "Input WAV file is empty"
        }

        outputFile.parentFile?.mkdirs()

        val wavBytes = inputFile.readBytes()

        require(
            String(wavBytes, 0, 4, Charsets.US_ASCII) == "RIFF" &&
                String(wavBytes, 8, 4, Charsets.US_ASCII) == "WAVE"
        ) {
            "Input must be a WAV file"
        }

        val sampleRate = readIntLE(wavBytes, 24)
        val channels = readShortLE(wavBytes, 22)
        val bitsPerSample = readShortLE(wavBytes, 34)

        require(
            sampleRate == SAMPLE_RATE &&
                channels == CHANNELS &&
                bitsPerSample == BITS_PER_SAMPLE
        ) {
            "DeepFilterNet requires 48 kHz, mono, 16-bit PCM. " +
                "Received $sampleRate Hz, $channels channels, " +
                "$bitsPerSample bits."
        }

        // This implementation assumes a standard 44-byte WAV header.
        val pcm = wavBytes.copyOfRange(
            WAV_HEADER_SIZE,
            wavBytes.size
        )

        require(pcm.size >= 2) {
            "WAV contains no PCM samples"
        }

        val filter = NativeDeepFilterNet(context.applicationContext)

        try {
            filter.setAttenuationLimit(
                attenuationDb.coerceIn(0f, 100f)
            )

            // The library reports the required frame size in bytes.
            val frameLength = filter.frameLength.toInt()

            require(frameLength > 0 && frameLength % 2 == 0) {
                "Invalid DeepFilterNet frame length: $frameLength"
            }

            val output = ByteArray(pcm.size)

            val frameBuffer = ByteBuffer
                .allocateDirect(frameLength)
                .order(ByteOrder.LITTLE_ENDIAN)

            var offset = 0

            while (offset < pcm.size) {
                currentCoroutineContext().ensureActive()

                val remaining = pcm.size - offset
                val bytesToCopy = minOf(frameLength, remaining)

                frameBuffer.clear()

                // Pad only the final incomplete frame.
                for (i in 0 until frameLength) {
                    frameBuffer.put(
                        if (i < bytesToCopy) pcm[offset + i]
                        else 0.toByte()
                    )
                }

                frameBuffer.flip()

                filter.processFrame(frameBuffer)

                frameBuffer.rewind()

                // Do not append padded bytes to the output file.
                frameBuffer.get(
                    output,
                    offset,
                    bytesToCopy
                )

                offset += bytesToCopy
            }

            writePcmWav(outputFile, output)

            check(outputFile.exists() && outputFile.length() > WAV_HEADER_SIZE) {
                "Cleaned WAV was not created successfully"
            }

            outputFile
        } finally {
            filter.release()
        }
    }

    private fun writePcmWav(
        file: File,
        pcm: ByteArray
    ) {
        RandomAccessFile(file, "rw").use { raf ->
            raf.setLength(0)

            raf.writeBytes("RIFF")
            writeIntLE(raf, 36 + pcm.size)
            raf.writeBytes("WAVE")

            raf.writeBytes("fmt ")
            writeIntLE(raf, 16)
            writeShortLE(raf, 1) // PCM
            writeShortLE(raf, CHANNELS)
            writeIntLE(raf, SAMPLE_RATE)

            writeIntLE(
                raf,
                SAMPLE_RATE * CHANNELS * BITS_PER_SAMPLE / 8
            )

            writeShortLE(
                raf,
                CHANNELS * BITS_PER_SAMPLE / 8
            )

            writeShortLE(raf, BITS_PER_SAMPLE)

            raf.writeBytes("data")
            writeIntLE(raf, pcm.size)
            raf.write(pcm)
        }
    }

    private fun readIntLE(
        bytes: ByteArray,
        offset: Int
    ): Int {
        return (bytes[offset].toInt() and 0xFF) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 3].toInt() and 0xFF) shl 24)
    }

    private fun readShortLE(
        bytes: ByteArray,
        offset: Int
    ): Int {
        return (bytes[offset].toInt() and 0xFF) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 8)
    }

    private fun writeIntLE(
        raf: RandomAccessFile,
        value: Int
    ) {
        raf.write(value and 0xFF)
        raf.write((value ushr 8) and 0xFF)
        raf.write((value ushr 16) and 0xFF)
        raf.write((value ushr 24) and 0xFF)
    }

    private fun writeShortLE(
        raf: RandomAccessFile,
        value: Int
    ) {
        raf.write(value and 0xFF)
        raf.write((value ushr 8) and 0xFF)
    }
}