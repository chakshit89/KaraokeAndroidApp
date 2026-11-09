package com.karoke.myoyasing.recordinglogics.cleanAudio

import com.karoke.myoyasing.recordinglogics.TarsosMelodyExtractor
import java.io.File
import java.io.RandomAccessFile

object CleanedMelodyGenerator {

    suspend fun generate(
        cleanedWav: File,
        melodyJson: File,
        sampleRate: Int = 48_000
    ): File {

        require(cleanedWav.exists()) {
            "Cleaned WAV file does not exist"
        }

        require(cleanedWav.length() > 44L) {
            "Cleaned WAV file is empty"
        }

        val extractor = TarsosMelodyExtractor(
            sampleRate = sampleRate,
            windowSize = 1024
        )

        try {
            RandomAccessFile(cleanedWav, "r").use { raf ->

                val header = ByteArray(44)
                raf.readFully(header)

                require(
                    String(header, 0, 4, Charsets.US_ASCII) == "RIFF" &&
                        String(header, 8, 4, Charsets.US_ASCII) == "WAVE"
                ) {
                    "Invalid WAV file"
                }

                val actualSampleRate = readIntLE(header, 24)
                val channels = readShortLE(header, 22)
                val bitsPerSample = readShortLE(header, 34)

                require(
                    actualSampleRate == sampleRate &&
                        channels == 1 &&
                        bitsPerSample == 16
                ) {
                    "Expected mono, 16-bit PCM at $sampleRate Hz"
                }

                val buffer = ByteArray(8192)

                while (true) {
                    val count = raf.read(buffer)

                    if (count <= 0) break

                    // Ensure the extractor receives complete PCM samples.
                    val evenCount = count - (count % 2)

                    if (evenCount > 0) {
                        extractor.acceptPcm(
                            bytes = buffer,
                            byteCount = evenCount
                        )
                    }
                }
            }

            return extractor.save(melodyJson)
        } catch (e: Exception) {
            throw e
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
}