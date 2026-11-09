package com.karoke.myoyasing.core.utils

object MediaUrlHelper {

    private const val BASE_URL = "http://163.223.186.213:2121"

    fun getFullUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null

        return if (url.startsWith("http://") ||
            url.startsWith("https://")
        ) {
            url
        } else {
            "${BASE_URL.trimEnd('/')}/${url.trimStart('/')}"
        }
    }
}