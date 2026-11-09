package com.karoke.myoyasing.recordinglogics

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import com.google.gson.GsonBuilder
import java.io.File
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.math.roundToInt

data class MelodyFrame(
    val timeMs: Long,
    val frequencyHz: Float?,
    val midi: Int?,
    val confidence: Float,
    val voiced: Boolean
)

data class MelodyNote(
    val startMs: Long,
    val endMs: Long,
    val midi: Int,
    val frequencyHz: Float,
    val confidence: Float
)

data class MelodyJson(
    val version: Int = 1,
    val sampleRate: Int,
    val algorithm: String = "FFT_YIN",
    val timeUnit: String = "ms",
    val frames: List<MelodyFrame>,
    val notes: List<MelodyNote>
)

class TarsosMelodyExtractor(
    private val sampleRate: Int,
    private val windowSize: Int = 1024
) {
    companion object {
        private const val MIN_HZ = 65f
        private const val MAX_HZ = 1000f
        private const val MIN_CONFIDENCE = 0.65f
        private const val MIN_RMS = 0.008
    }

    private val frames = mutableListOf<MelodyFrame>()
    private val window = ShortArray(windowSize)
    private val floatWindow = FloatArray(windowSize)

    private var windowFill = 0
    private var totalSamplesProcessed = 0L
    private var currentWindowStartSample = 0L
    private var finished = false

    private val audioFormat = TarsosDSPAudioFormat(
        sampleRate.toFloat(),
        16,
        1,
        true,
        false
    )

    private val processor = PitchProcessor(
        PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
        sampleRate.toFloat(),
        windowSize,
        PitchDetectionHandler { result, _ ->

            val hz = result.pitch
            val confidence = result.probability

            val energy = floatWindow.sumOf {
                it.toDouble() * it.toDouble()
            }
            val rms = sqrt(energy / windowSize)

            val voiced = hz.isFinite() &&
                hz in MIN_HZ..MAX_HZ &&
                confidence >= MIN_CONFIDENCE &&
                rms >= MIN_RMS

            val validHz = if (voiced) hz else null

            val midi = validHz?.let {
                (69.0 + 12.0 * ln(it / 440.0) / ln(2.0))
                    .roundToInt()
            }

            val centerSample =
                currentWindowStartSample + windowSize / 2L

            frames.add(
                MelodyFrame(
                    timeMs = centerSample * 1000L / sampleRate,
                    frequencyHz = validHz,
                    midi = midi,
                    confidence = confidence.coerceIn(0f, 1f),
                    voiced = voiced
                )
            )
        }
    )

    /**
     * Call this from KaraokeRecorder's existing AudioRecord read loop.
     * Pass only samples actually returned by AudioRecord.read().
     */
    fun acceptPcm(samples: ShortArray, count: Int) {
        check(!finished) { "Melody extraction is already finished" }

        require(count in 0..samples.size)

        for (i in 0 until count) {
            window[windowFill++] = samples[i]

            if (windowFill == windowSize) {
                processWindow()
                windowFill = 0
            }
        }
    }

    private fun processWindow() {
        currentWindowStartSample = totalSamplesProcessed

        for (i in 0 until windowSize) {
            floatWindow[i] = window[i] / 32768f
        }

        val event = AudioEvent(audioFormat)
        event.setFloatBuffer(floatWindow.copyOf())
        event.setBytesProcessed(totalSamplesProcessed * 2L)
        event.setBytesProcessing(windowSize * 2)

        processor.process(event)

        totalSamplesProcessed += windowSize
    }

    /**
     * Call only after the microphone read loop has completely stopped.
     */
    fun save(outputFile: File): File {
        check(!finished) { "Melody was already saved" }
        finished = true

        // Ignore a partial final window to avoid padding that could
        // create a false pitch estimate.
        val melody = MelodyJson(
            sampleRate = sampleRate,
            frames = frames.toList(),
            notes = mergeNotes(frames)
        )

        outputFile.parentFile?.let {
            check(it.exists() || it.mkdirs()) {
                "Cannot create melody output directory"
            }
        }

        outputFile.writeText(
            GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create()
                .toJson(melody)
        )

        processor.processingFinished()
        return outputFile
    }

    private fun mergeNotes(
        input: List<MelodyFrame>
    ): List<MelodyNote> {
        val notes = mutableListOf<MelodyNote>()

        var midi: Int? = null
        var startMs = 0L
        var lastMs = 0L
        var frequencies = mutableListOf<Float>()
        var confidences = mutableListOf<Float>()

        fun flush() {
            val pitch = midi

            if (pitch != null && frequencies.isNotEmpty()) {
                notes.add(
                    MelodyNote(
                        startMs = startMs,
                        endMs = lastMs +
                            windowSize * 1000L / sampleRate,
                        midi = pitch,
                        frequencyHz =
                            frequencies.average().toFloat(),
                        confidence =
                            confidences.average().toFloat()
                    )
                )
            }

            midi = null
            frequencies = mutableListOf()
            confidences = mutableListOf()
        }

        for (frame in input) {
            val pitch = frame.midi
            val hz = frame.frequencyHz

            if (!frame.voiced || pitch == null || hz == null) {
                flush()
                continue
            }

            if (midi == pitch && frame.timeMs - lastMs <= 100L) {
                lastMs = frame.timeMs
                frequencies.add(hz)
                confidences.add(frame.confidence)
            } else {
                flush()
                midi = pitch
                startMs = frame.timeMs
                lastMs = frame.timeMs
                frequencies.add(hz)
                confidences.add(frame.confidence)
            }
        }

        flush()
        return notes
    }
    fun acceptPcm(bytes: ByteArray, byteCount: Int) {
        val sampleCount = byteCount / 2
        val samples = ShortArray(sampleCount)

        for (i in 0 until sampleCount) {
            val low = bytes[i * 2].toInt() and 0xFF
            val high = bytes[i * 2 + 1].toInt()

            samples[i] = ((high shl 8) or low).toShort()
        }

        // Calls your existing ShortArray-based function.
        acceptPcm(samples, sampleCount)
    }
}
