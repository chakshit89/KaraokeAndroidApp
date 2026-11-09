package com.karoke.myoyasing.core.utils.recordinglogic

fun parseInlineTimestamp(
    minutes: String,
    seconds: String,
    fraction: String
): Long {

    val minuteMs =
        minutes.toLong() * 60_000L

    val secondMs =
        seconds.toLong() * 1_000L

    val fractionMs =
        when (fraction.length) {

            0 -> 0L

            1 ->
                fraction.toLong() * 100L

            2 ->
                fraction.toLong() * 10L

            else ->
                fraction
                    .take(3)
                    .toLong()
        }

    return minuteMs +
        secondMs +
        fractionMs
}