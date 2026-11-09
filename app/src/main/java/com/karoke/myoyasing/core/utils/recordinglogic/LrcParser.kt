package com.karoke.myoyasing.core.utils.recordinglogic

object LrcParser {

    private val timeRegex =
        Regex("""\[(\d{2}):(\d{2})(?:[.:](\d{1,3}))?]""")

    fun parse(lrcText: String): List<LyricsLine> {

        val result = mutableListOf<LyricsLine>()

        val regex =
            Regex("""\[(\d{2}):(\d{2})(?:\.(\d{1,3}))?](.*)""")

        lrcText
            .lineSequence()
            .forEach { line ->

                val match =
                    regex.matchEntire(line.trim())
                        ?: return@forEach

                val minutes =
                    match.groupValues[1].toLong()

                val seconds =
                    match.groupValues[2].toLong()

                val fraction =
                    match.groupValues[3]

                val milliseconds =
                    when (fraction.length) {

                        0 -> 0L

                        1 ->
                            fraction
                                .toLong() * 100

                        2 ->
                            fraction
                                .toLong() * 10

                        else ->
                            fraction
                                .take(3)
                                .toLong()
                    }

                val timeMs =
                    minutes * 60_000L +
                            seconds * 1_000L +
                            milliseconds

                val text =
                    match.groupValues[4].trim()

                /*
                 * Don't add empty lyric lines.
                 */
                if (text.isNotBlank()) {

                    result.add(
                        LyricsLine(
                            timeMs = timeMs,
                            text = text
                        )
                    )
                }
            }

        return result.sortedBy {
            it.timeMs
        }
    }
}