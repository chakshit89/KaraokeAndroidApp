package com.karoke.myoyasing.core.utils

import com.karoke.myoyasing.di.network.MainRetrofitClient

fun String?.toAbsoluteUrl(): String? {
    val value = this
        ?.trim()
        ?.takeIf {
            it.isNotEmpty() &&
            !it.equals("null", ignoreCase = true) &&
            !it.equals("string", ignoreCase = true)
        }
        ?: return null

    if (
        value.startsWith("https://", ignoreCase = true) ||
        value.startsWith("http://", ignoreCase = true)
    ) {
        return value
    }

    return MainRetrofitClient.BASE_URL.trimEnd('/') +
        "/" + value.trimStart('/')
}