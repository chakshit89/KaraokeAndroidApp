package com.karoke.myoyasing.core.scoringlogic

import com.google.gson.Gson
import com.karoke.myoyasing.recordinglogics.MelodyJson
import com.karoke.myoyasing.recordinglogics.MelodyNote
import java.io.File
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max

data class KaraokeScoreResult(
    val pitchAccuracy: Float,
    val timingAccuracy: Float,
    val noteAccuracy: Float,
    val coverage: Float,
    val overallScore: Float,
    val matchedNotes: Int,
    val referenceNotes: Int,
    val averagePitchErrorCents: Float,
    val averageOnsetErrorMs: Float
)

class KaraokeMelodyScorer {

    companion object {
        private const val MAX_ONSET_ERROR_MS = 300L

        private const val FULL_PITCH_CENTS = 50.0

        private const val MAX_PITCH_ERROR_CENTS = 100.0

        private const val FULL_TIMING_ERROR_MS = 50.0

        private const val MAX_TIMING_ERROR_MS = 200.0

        private const val MAX_DURATION_ERROR_MS = 300.0
    }

    private data class NoteMatch(
        val reference: MelodyNote,
        val user: MelodyNote,
        val pitchErrorCents: Double,
        val onsetErrorMs: Long,
        val durationErrorMs: Long
    )

    fun score(
        reference: MelodyJson,
        user: MelodyJson
    ): KaraokeScoreResult {

        val referenceNotes = reference.notes
            .filter { it.endMs > it.startMs }
            .sortedBy { it.startMs }

        val userNotes = user.notes
            .filter { it.endMs > it.startMs }
            .sortedBy { it.startMs }

        if (referenceNotes.isEmpty()) {
            return KaraokeScoreResult(
                pitchAccuracy = 0f,
                timingAccuracy = 0f,
                noteAccuracy = 0f,
                coverage = 0f,
                overallScore = 0f,
                matchedNotes = 0,
                referenceNotes = 0,
                averagePitchErrorCents = 0f,
                averageOnsetErrorMs = 0f
            )
        }

        val usedUserNotes = mutableSetOf<Int>()
        val matches = mutableListOf<NoteMatch>()

        for (referenceNote in referenceNotes) {

            val candidates = userNotes.indices
                .filter { index ->
                    index !in usedUserNotes &&
                    abs(
                        userNotes[index].startMs -
                            referenceNote.startMs
                    ) <= MAX_ONSET_ERROR_MS
                }

            val bestIndex = candidates.minByOrNull { index ->
                abs(
                    userNotes[index].startMs -
                        referenceNote.startMs
                )
            } ?: continue

            val userNote = userNotes[bestIndex]

            usedUserNotes.add(bestIndex)

            val pitchErrorCents = abs(
                1200.0 * ln(
                    userNote.frequencyHz.toDouble() /
                        referenceNote.frequencyHz.toDouble()
                ) / ln(2.0)
            )

            val onsetError = abs(
                userNote.startMs - referenceNote.startMs
            )

            val referenceDuration =
                referenceNote.endMs - referenceNote.startMs

            val userDuration =
                userNote.endMs - userNote.startMs

            matches.add(
                NoteMatch(
                    reference = referenceNote,
                    user = userNote,
                    pitchErrorCents = pitchErrorCents,
                    onsetErrorMs = onsetError,
                    durationErrorMs = abs(
                        userDuration - referenceDuration
                    )
                )
            )
        }

        if (matches.isEmpty()) {
            return KaraokeScoreResult(
                pitchAccuracy = 0f,
                timingAccuracy = 0f,
                noteAccuracy = 0f,
                coverage = 0f,
                overallScore = 0f,
                matchedNotes = 0,
                referenceNotes = referenceNotes.size,
                averagePitchErrorCents = 0f,
                averageOnsetErrorMs = 0f
            )
        }

        val pitchAccuracy = matches
            .map { match ->
                when {
                    match.pitchErrorCents <= FULL_PITCH_CENTS ->
                        100.0

                    match.pitchErrorCents >= MAX_PITCH_ERROR_CENTS ->
                        0.0

                    else ->
                        100.0 *
                            (MAX_PITCH_ERROR_CENTS -
                                match.pitchErrorCents) /
                            (MAX_PITCH_ERROR_CENTS -
                                FULL_PITCH_CENTS)
                }
            }
            .average()
            .toFloat()

        val timingAccuracy = matches
            .map { match ->

                val onsetScore = (
                    100.0 *
                        (
                            MAX_TIMING_ERROR_MS -
                                match.onsetErrorMs
                        ) /
                        (
                            MAX_TIMING_ERROR_MS -
                                FULL_TIMING_ERROR_MS
                        )
                    ).coerceIn(0.0, 100.0)

                val durationScore = (
                    100.0 *
                        (
                            1.0 -
                                match.durationErrorMs.toDouble() /
                                MAX_DURATION_ERROR_MS
                        )
                    ).coerceIn(0.0, 100.0)

                onsetScore * 0.75 + durationScore * 0.25
            }
            .average()
            .toFloat()

        val coverage = (
            matches.size.toFloat() /
                referenceNotes.size
            ).coerceIn(0f, 1f) * 100f

        val noteAccuracy = coverage
        val overallScore = (
            pitchAccuracy * 0.50f +
                timingAccuracy * 0.30f +
                coverage * 0.20f
            ).coerceIn(0f, 100f)

        return KaraokeScoreResult(
            pitchAccuracy = pitchAccuracy,
            timingAccuracy = timingAccuracy,
            noteAccuracy = noteAccuracy,
            coverage = coverage,
            overallScore = overallScore,
            matchedNotes = matches.size,
            referenceNotes = referenceNotes.size,
            averagePitchErrorCents = matches
                .map { it.pitchErrorCents }
                .average()
                .toFloat(),
            averageOnsetErrorMs = matches
                .map { it.onsetErrorMs.toDouble() }
                .average()
                .toFloat()
        )
    }
}


//val gson = Gson()
//
//val referenceMelody = gson.fromJson(
//    File(referenceJsonPath).readText(),
//    MelodyJson::class.java
//)
//
//val userMelody = gson.fromJson(
//    File(userJsonPath).readText(),
//    MelodyJson::class.java
//)
//
//val scorer = KaraokeMelodyScorer()
//
//val result = scorer.score(
//    reference = referenceMelody,
//    user = userMelody
//)
//
//println("Pitch accuracy: ${result.pitchAccuracy}%")
//println("Timing accuracy: ${result.timingAccuracy}%")
//println("Coverage: ${result.coverage}%")
//println("Overall score: ${result.overallScore}")
//println("Matched notes: ${result.matchedNotes}")
//println("Average pitch error: ${result.averagePitchErrorCents} cents")
//println("Average onset error: ${result.averageOnsetErrorMs} ms")