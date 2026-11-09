package com.karoke.myoyasing.presentation.featureUI.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressSection(
    positionMs: Long,
    durationMs: Long
) {

    val progress =
        if (durationMs > 0) {
            (positionMs.toFloat() /
                    durationMs.toFloat())
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp
                )
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text =
                    formatTime(positionMs),
                color = TextWhite,
                fontSize = 13.sp
            )

            Text(
                text =
                    formatTime(durationMs),
                color = TextMuted,
                fontSize = 13.sp
            )
        }

        Spacer(
            modifier =
                Modifier.height(7.dp)
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(
                        RoundedCornerShape(
                            10.dp
                        )
                    )
                    .background(
                        PurpleDark
                    )
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            PurpleBright
                        )
            )
        }
    }
}

private fun formatTime(
    milliseconds: Long
): String {

    if (milliseconds <= 0) {
        return "00:00"
    }

    val totalSeconds =
        milliseconds / 1000

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60

    return "%02d:%02d".format(
        minutes,
        seconds
    )
}