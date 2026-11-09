package com.karoke.myoyasing.presentation.featureUI.recording

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karoke.myoyasing.core.utils.recordinglogic.LyricsLine
import com.karoke.myoyasing.core.utils.recordinglogic.TimedWord
import com.karoke.myoyasing.core.utils.recordinglogic.parseInlineTimestamp

@Composable
fun LyricsSection(
    lyrics: List<LyricsLine>,
    currentLyricIndex: Int,
    positionMs: Long,
    modifier: Modifier = Modifier
) {
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Automatically scroll to the active lyric.
    // Do not scroll before the first lyric starts.
    LaunchedEffect(currentLyricIndex, lyrics.size) {
        if (
            currentLyricIndex >= 0 &&
            currentLyricIndex < lyrics.size
        ) {
            listState.animateScrollToItem(
                index = currentLyricIndex,
                scrollOffset = -120
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (lyrics.isEmpty()) {
            return@Box
        }

        LazyColumn(
            state = listState,
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            itemsIndexed(
                items = lyrics,
                key = { index, lyric ->
                    "${lyric.timeMs}_$index"
                }
            ) { index, lyric ->

                when {
                    // Current lyric: retain word-level highlighting.
                    index == currentLyricIndex -> {
                        KaraokeCurrentLine(
                            lyric = lyric,
                            positionMs = positionMs
                        )
                    }

                    // Previous lyrics: visible in muted color.
                    currentLyricIndex >= 0 &&
                            index < currentLyricIndex -> {
                        Text(
                            text = cleanLyricText(lyric.text),
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(0.55f),
                            color = TextMuted,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }

                    // Upcoming lyrics, or all lyrics before playback
                    // reaches the first line.
                    else -> {
                        Text(
                            text = cleanLyricText(lyric.text),
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(0.45f),
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun KaraokeCurrentLine(
    lyric: LyricsLine,
    positionMs: Long
) {
    val words = remember(lyric.text, lyric.timeMs) {
        parseTimedWords(
            lyricText = lyric.text,
            lineStartMs = lyric.timeMs
        )
    }


    if (words.isEmpty()) {
        Text(
            text = cleanLyricText(lyric.text),
            color = TextWhite,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        return
    }

    Text(
        text = buildAnnotatedString {
            words.forEachIndexed { index, word ->
                val nextWord = words.getOrNull(index + 1)

                // The current word remains active until the next word starts.
                val isCurrentlySinging =
                    positionMs >= word.startMs &&
                            (nextWord == null || positionMs < nextWord.startMs)

                withStyle(
                    SpanStyle(
                        color = if (isCurrentlySinging) {
                            PurpleBright
                        } else {
                            TextWhite
                        },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(word.text)

                    if (index < words.lastIndex) {
                        append(" ")
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        lineHeight = 40.sp
    )

}
private fun cleanLyricText(text: String): String {
    return text
        .replace(
            Regex("""<\d{2}:\d{2}(?:\.\d{1,3})?>"""),
            ""
        )
        .replace(Regex("""\s+"""), " ")
        .trim()
}

private fun parseTimedWords(
    lyricText: String,
    lineStartMs: Long
): List<TimedWord> {

    val timestampRegex = Regex(
        """<(\d{2}):(\d{2})(?:\.(\d{1,3}))?>"""
    )

    val matches = timestampRegex.findAll(lyricText).toList()
    android.util.Log.d(
        "KaraokeLyrics",
        "Original lyric: $lyricText"
    )

    android.util.Log.d(
        "KaraokeLyrics",
        "Word timestamp count: ${matches.size}"
    )

    android.util.Log.d(
        "KaraokeLyrics",
        "Detected timestamps: ${matches.map { it.value }}"
    )
    if (matches.isEmpty()) {
        return emptyList()
    }

    val result = mutableListOf<TimedWord>()
    android.util.Log.d(
        "KaraokeLyrics",
        "Parsed words: ${result.map { "${it.text} @ ${it.startMs}ms" }}"
    )
    val firstMatch = matches.first()

    val textBeforeFirstTimestamp = lyricText
        .substring(0, firstMatch.range.first)
        .trim()

    if (textBeforeFirstTimestamp.isNotEmpty()) {
        result.add(
            TimedWord(
                startMs = lineStartMs,
                text = textBeforeFirstTimestamp
            )
        )
    }

    // Each timestamp applies to the text immediately following it.
    matches.forEachIndexed { index, match ->
        val startMs = parseInlineTimestamp(
            match.groupValues[1],
            match.groupValues[2],
            match.groupValues[3]
        )

        val contentStart = match.range.last + 1

        val contentEnd = matches.getOrNull(index + 1)
            ?.range
            ?.first
            ?: lyricText.length

        if (contentStart < contentEnd) {
            val word = lyricText
                .substring(contentStart, contentEnd)
                .trim()

            if (word.isNotEmpty()) {
                result.add(
                    TimedWord(
                        startMs = startMs,
                        text = word
                    )
                )
            }
        }
    }

    return result.sortedBy { it.startMs }
}