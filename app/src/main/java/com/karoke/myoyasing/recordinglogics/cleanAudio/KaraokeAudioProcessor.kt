package com.karoke.myoyasing.recordinglogics.cleanAudio

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class KaraokeAudioProcessor(
    context: Context
) {

    private val deepFilter = DeepFilterNetProcessor(
        context.applicationContext
    )

    suspend fun processRecording(
        originalWav: File
    ): Pair<File, File> = withContext(Dispatchers.IO) {

        val directory = originalWav.parentFile
            ?: error("Recording directory is missing")

        val cleanedWav = File(
            directory,
            "vocals_cleaned.wav"
        )

        val cleanedMelody = File(
            directory,
            "vocals_cleaned_melody.json"
        )

        Log.d(
            "KaraokeAudioProcessor",
            "Cleaning vocals: ${originalWav.absolutePath}"
        )

        deepFilter.cleanWav(
            inputFile = originalWav,
            outputFile = cleanedWav,
            attenuationDb = 30f
        )

        Log.d(
            "KaraokeAudioProcessor",
            "Cleaned vocals: ${cleanedWav.absolutePath}"
        )

        CleanedMelodyGenerator.generate(
            cleanedWav = cleanedWav,
            melodyJson = cleanedMelody,
            sampleRate = 48_000
        )

        Log.d(
            "KaraokeAudioProcessor",
            "Melody JSON: ${cleanedMelody.absolutePath}"
        )

        cleanedWav to cleanedMelody
    }
}