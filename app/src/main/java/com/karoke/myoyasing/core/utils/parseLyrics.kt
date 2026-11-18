package com.karoke.myoyasing.core.utils

import com.karoke.myoyasing.core.utils.recordinglogic.LyricsLine

fun parseLyrics(
    lyricsText: String
): List<LyricsLine> {

    val regex =
        Regex("""\[(\d{2}):(\d{2})(?:\.(\d+))?](.*)""")

    return lyricsText
        .lines()
        .mapNotNull { line ->

            val match =
                regex.find(line)
                    ?: return@mapNotNull null

            val minute =
                match.groupValues[1]
                    .toLong()

            val second =
                match.groupValues[2]
                    .toLong()

            val milli =
                match.groupValues[3]
                    .ifBlank { "0" }
                    .toLong()

            val text =
                match.groupValues[4]
                    .trim()

            LyricsLine(
                timeMs =
                minute * 60000L +
                second * 1000L +
                milli,

                text = text
            )
        }
}
