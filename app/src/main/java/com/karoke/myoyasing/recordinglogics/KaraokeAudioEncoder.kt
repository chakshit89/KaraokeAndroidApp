package com.karoke.myoyasing.recordinglogics

import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile

object KaraokeAudioEncoder {

    fun encodeWav(
        pcmFile: File,
        outputFile: File,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int = 16
    ): File {

        require(pcmFile.exists()) {
            "PCM file does not exist"
        }

        outputFile.parentFile?.mkdirs()

        val dataSize =
            pcmFile.length()

        require(
            dataSize <= Int.MAX_VALUE.toLong() - 36
        ) {
            "WAV file is too large"
        }

        RandomAccessFile(
            outputFile,
            "rw"
        ).use { output ->

            output.setLength(0)

            output.writeBytes("RIFF")

            writeIntLE(
                output,
                (36 + dataSize).toInt()
            )

            output.writeBytes("WAVE")

            output.writeBytes("fmt ")

            writeIntLE(
                output,
                16
            )

            writeShortLE(
                output,
                1
            )

            writeShortLE(
                output,
                channels
            )

            writeIntLE(
                output,
                sampleRate
            )

            val byteRate =
                sampleRate *
                        channels *
                        bitsPerSample /
                        8

            writeIntLE(
                output,
                byteRate
            )

            val blockAlign =
                channels *
                        bitsPerSample /
                        8

            writeShortLE(
                output,
                blockAlign
            )

            writeShortLE(
                output,
                bitsPerSample
            )

            output.writeBytes("data")

            writeIntLE(
                output,
                dataSize.toInt()
            )

            FileInputStream(
                pcmFile
            ).use { input ->

                val buffer =
                    ByteArray(64 * 1024)

                while (true) {

                    val read =
                        input.read(buffer)

                    if (read <= 0) {
                        break
                    }

                    output.write(
                        buffer,
                        0,
                        read
                    )
                }
            }
        }

        return outputFile
    }

    private fun writeIntLE(
        output: RandomAccessFile,
        value: Int
    ) {

        output.write(
            value and 0xFF
        )

        output.write(
            (value shr 8) and 0xFF
        )

        output.write(
            (value shr 16) and 0xFF
        )

        output.write(
            (value shr 24) and 0xFF
        )
    }

    private fun writeShortLE(
        output: RandomAccessFile,
        value: Int
    ) {

        output.write(
            value and 0xFF
        )

        output.write(
            (value shr 8) and 0xFF
        )
    }
}