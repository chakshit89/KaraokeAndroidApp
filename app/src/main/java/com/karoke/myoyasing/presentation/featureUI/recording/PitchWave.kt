
package com.example.pitchmasterbeta.ui.workspace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun PitchWave(
    isPlaying: Boolean = false,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableFloatStateOf(0f) }

    // Animate only while music is playing.
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                phase += 0.10f
                if (phase > 1000f) phase = 0f
                delay(16L)
            }
        } else {
            phase = 0f
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val centerY = size.height * 0.55f
        val dp = 1.dp.toPx()

        // Dotted horizontal center line
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = Color(0xFF6D4A78),
                start = Offset(x, centerY),
                end = Offset(x + 5 * dp, centerY),
                strokeWidth = 2 * dp
            )
            x += 14 * dp
        }

        // Animated waveform moving across the line
        val path = Path()
        val amplitude = if (isPlaying) 35 * dp else 2 * dp
        val waveCycles = 2.5f

        val step = 2 * dp
        var waveX = 0f
        var firstPoint = true

        while (waveX <= size.width) {
            val normalizedX = waveX / size.width
            val envelope = sin(normalizedX * Math.PI).toFloat()

            val waveY = centerY +
                    sin(
                        normalizedX * waveCycles * 2 * Math.PI -
                                phase
                    ).toFloat() * amplitude * envelope

            if (firstPoint) {
                path.moveTo(waveX, waveY)
                firstPoint = false
            } else {
                path.lineTo(waveX, waveY)
            }

            waveX += step
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF9C5AB3),
                    Color(0xFFE9B3FF),
                    Color(0xFF9C5AB3)
                )
            ),
            style = Stroke(
                width = 3 * dp,
                cap = StrokeCap.Round
            )
        )

        // Moving glowing indicator
        val markerX = if (isPlaying) {
            (phase * 30 * dp) % size.width
        } else {
            size.width * 0.5f
        }

        val markerY = centerY +
                sin(
                    (markerX / size.width) *
                            waveCycles * 2 * Math.PI - phase
                ).toFloat() * amplitude *
                sin(markerX / size.width * Math.PI).toFloat()

        drawCircle(
            color = Color(0x55E9B3FF),
            radius = 13 * dp,
            center = Offset(markerX, markerY)
        )

        drawCircle(
            color = Color(0xFFE9B3FF),
            radius = 5 * dp,
            center = Offset(markerX, markerY)
        )
    }
}
