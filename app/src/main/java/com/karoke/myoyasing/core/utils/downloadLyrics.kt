package com.karoke.myoyasing.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

suspend fun downloadLyrics(
    lyricsUrl: String
): String {

    return withContext(Dispatchers.IO) {

        URL(lyricsUrl)
            .readText()
    }
}