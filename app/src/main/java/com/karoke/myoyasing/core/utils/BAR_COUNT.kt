
package com.karoke.myoyasing.core.utils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val SplashPink = Color(0xFFFF35D3)
private val SplashPurple = Color(0xFFB44CFF)
private val SplashCyan = Color(0xFF00DFFF)

private const val BAR_COUNT = 41

@Composable
fun NeonEqualizer(
    isPlaying: Boolean,
    audioSessionId: Int,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(
        label = "neon_equalizer"
    )

    val heights = (0 until BAR_COUNT).map { index ->

        // Increased height variation
        val base = 25f + ((index * 23 + index % 5 * 17) % 65)

        val height by transition.animateFloat(
            initialValue = (base * 0.15f).coerceAtLeast(8f),
            targetValue = base,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 180 + (index % 5) * 45,
                    delayMillis = (index % 4) * 25,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "eq_$index"
        )

        height
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.spacedBy(
            2.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.Bottom
    ) {
        heights.forEachIndexed { index, height ->

            val barColor = when {
                index % 5 == 0 -> SplashCyan
                index % 3 == 0 -> SplashPink
                else -> SplashPurple
            }

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(height.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                barColor.copy(alpha = 0.95f),
                                barColor.copy(alpha = 0.35f)
                            )
                        ),
                        shape = RoundedCornerShape(5.dp)
                    )
            )
        }
    }
}