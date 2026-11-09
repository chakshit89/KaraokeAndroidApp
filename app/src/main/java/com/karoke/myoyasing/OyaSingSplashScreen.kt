package com.karoke.myoyasing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private val SplashNavy = Color(0xFF050B2D)
private val SplashDeepNavy = Color(0xFF02051A)
private val SplashPink = Color(0xFFFF35D3)
private val SplashPurple = Color(0xFFB44CFF)
private val SplashCyan = Color(0xFF00DFFF)
private val SplashWhite = Color(0xFFFFF3FF)

@Composable
fun OyaSingSplashScreen(
    onFinished: () -> Unit
) {
    var showLogo by remember { mutableStateOf(false) }
    var showNotes by remember { mutableStateOf(false) }
    var showOya by remember { mutableStateOf(false) }
    var showSing by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }

    val logoScale = remember { Animatable(0.35f) }
    val logoRotation = remember { Animatable(-12f) }
    val infinite = rememberInfiniteTransition(label = "splash_motion")
    val floatY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_float"
    )
    val haloPulse by infinite.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_pulse"
    )

    LaunchedEffect(Unit) {
        delay(180)
        showLogo = true
        launch {
            logoScale.animateTo(
                1f,
                spring(dampingRatio = 0.48f, stiffness = Spring.StiffnessMediumLow)
            )
        }
        launch {
            logoRotation.animateTo(
                0f,
                spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium)
            )
        }
        delay(250)
        showNotes = true
        delay(350)
        showOya = true
        delay(260)
        showSing = true
        delay(250)
        showTagline = true
        // Keep the splash visible long enough to enjoy the animation.
        delay(2100)
        onFinished()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF101044),
                        SplashNavy,
                        Color(0xFF07082A),
                        SplashDeepNavy
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val screenWidth = maxWidth
        val logoSize = (screenWidth * 0.68f).coerceIn(230.dp, 310.dp)


//        Box(
//            Modifier
//                .size(300.dp)
//                .scale(haloPulse)
//                .blur(75.dp)
//                .background(
//                    SplashPurple.copy(alpha = 0.22f),
//                    CircleShape
//                )
//                .align(Alignment.Center)
//        )

        NeonBackdrop()
        SplashParticles()


        if (showNotes) {
            FloatingMusicNote(
                note = "♫",
                color = SplashPink,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (-screenWidth * 0.32f), y = (-115).dp),
                delayMillis = 0
            )
            FloatingMusicNote(
                note = "♪",
                color = SplashPurple,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (screenWidth * 0.31f), y = (-145).dp),
                delayMillis = 130
            )
            FloatingMusicNote(
                note = "♫",
                color = SplashCyan,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (-screenWidth * 0.38f), y = (80).dp),
                delayMillis = 260
            )
            FloatingMusicNote(
                note = "♪",
                color = SplashPink,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (screenWidth * 0.36f), y = (55).dp),
                delayMillis = 390
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(logoSize)
                    .graphicsLayer {
                        translationY = floatY
                        rotationZ = logoRotation.value
                    },
                contentAlignment = Alignment.Center
            ) {
                // Glow behind the app icon.
                Box(
                    Modifier
                        .size(logoSize * 0.76f)
                        .blur(38.dp)
                        .background(SplashPink.copy(alpha = 0.24f), CircleShape)
                )
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                SplashPink,
                                SplashPurple,
                                SplashCyan,
                                SplashPink
                            )
                        ),
                        radius = size.minDimension * 0.47f,
                        style = Stroke(width = 2.2.dp.toPx())
                    )
                    drawCircle(
                        color = SplashCyan.copy(alpha = 0.25f),
                        radius = size.minDimension * 0.40f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                this@Column.AnimatedVisibility(
                    visible = showLogo,
                    enter = fadeIn(tween(450)) + scaleIn(
                        initialScale = 0.25f,
                        animationSpec = spring(
                            dampingRatio = 0.48f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.splash_icon),
                        contentDescription = "Oya Sing logo",
                        modifier = Modifier
                            .size(logoSize * 0.78f)
                            .scale(logoScale.value)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showOya) {
                    AnimatedWord(
                        word = "Oya",
                        gradient = listOf(SplashPink, Color(0xFFFFA8E8)),
                        delayMillis = 0
                    )
                }
                Spacer(Modifier.width(9.dp))
                if (showSing) {
                    AnimatedWord(
                        word = "Sing",
                        gradient = listOf(SplashCyan, SplashPurple),
                        delayMillis = 0
                    )
                }
            }

            Spacer(Modifier.height(13.dp))

            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(tween(650)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(650, easing = FastOutSlowInEasing)
                )
            ) {
                Text(
                    text = "Your music. Your voice. Your stage.",
                    color = SplashWhite.copy(alpha = 0.86f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.1.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(56.dp))

            AnimatedEqualizer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            )
        }
    }
}

@Composable
private fun AnimatedWord(
    word: String,
    gradient: List<Color>,
    delayMillis: Long
) {
    var visible by remember(word) { mutableStateOf(false) }
    val scale = remember(word) { Animatable(0.55f) }
    val rotation = remember(word) { Animatable(8f) }

    LaunchedEffect(word) {
        delay(delayMillis)
        visible = true
        launch {
            scale.animateTo(
                1f,
                spring(dampingRatio = 0.48f, stiffness = Spring.StiffnessMedium)
            )
        }
        launch {
            rotation.animateTo(
                0f,
                spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium)
            )
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(280)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(380, easing = FastOutSlowInEasing)
        )
    ) {
        Text(
            text = word,
            style = TextStyle(
                brush = Brush.linearGradient(gradient),
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1.3).sp
            ),
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                rotationZ = rotation.value
            }
        )
    }
}

@Composable
private fun FloatingMusicNote(
    note: String,
    color: Color,
    modifier: Modifier = Modifier,
    delayMillis: Long
) {
    var visible by remember { mutableStateOf(false) }
    val popScale = remember { Animatable(0f) }
    val infinite = rememberInfiniteTransition(label = "note_float")
    val floatY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -13f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "note_y"
    )

    LaunchedEffect(Unit) {
        delay(delayMillis)
        visible = true
        popScale.animateTo(
            1f,
            spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMedium)
        )
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier.graphicsLayer { translationY = floatY },
        enter = fadeIn(tween(180)) + scaleIn(
            initialScale = 0.05f,
            animationSpec = spring(
                dampingRatio = 0.3f,
                stiffness = Spring.StiffnessMedium
            )
        )
    ) {
        Text(
            text = note,
            color = color,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .scale(popScale.value)
                .graphicsLayer {
                    shadowElevation = 18f
                }
        )
    }
}

@Composable
private fun AnimatedEqualizer(
    modifier: Modifier = Modifier
) {
    val barCount = 35
    val transition = rememberInfiniteTransition(label = "equalizer")
    val heights = (0 until barCount).map { index ->
        val base = 12f + ((index * 17 + index % 4 * 11) % 43)
        val height by transition.animateFloat(
            initialValue = (base * 0.35f).coerceAtLeast(5f),
            targetValue = base,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 350 + (index % 7) * 95,
                    delayMillis = (index % 6) * 70,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "eq_$index"
        )
        height
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
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
                    .width(4.dp)
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


@Composable
private fun NeonBackdrop() {
    val infinite = rememberInfiniteTransition(label = "aurora_waves")

    val wavePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7000,
                easing = androidx.compose.animation.core.LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val wavePulse by infinite.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_pulse"
    )

    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Keep the waves behind the logo and concentrated around it.
        val centerX = w * 0.5f
        val centerY = h * 0.46f

        val phase = wavePhase
        val pulse = wavePulse

        // Draw broad, flowing ribbons using smooth cubic curves.
        for (layer in 0 until 5) {
            val layerOffset = layer * 0.19f
            val amplitude = h * (0.025f + layer * 0.009f) * pulse

            val path = androidx.compose.ui.graphics.Path()

            val startY = centerY +
                    sin(phase + layerOffset) * amplitude

            path.moveTo(-w * 0.1f, startY)

            val controlY1 = centerY +
                    sin(phase + 1.2f + layerOffset) * amplitude * 2f

            val controlY2 = centerY +
                    sin(phase + 2.5f + layerOffset) * amplitude * 2f

            val endY = centerY +
                    sin(phase + 3.8f + layerOffset) * amplitude

            path.cubicTo(
                w * 0.2f,
                controlY1,
                w * 0.8f,
                controlY2,
                w * 1.1f,
                endY
            )

            // Give each ribbon a flowing lower edge.
            path.cubicTo(
                w * 0.8f,
                endY + amplitude * 2.4f,
                w * 0.2f,
                startY + amplitude * 2.4f,
                -w * 0.1f,
                startY
            )

            path.close()

            val waveColors = when (layer % 3) {
                0 -> listOf(
                    SplashPink.copy(alpha = 0.0f),
                    SplashPink.copy(alpha = 0.20f),
                    SplashPurple.copy(alpha = 0.30f),
                    SplashCyan.copy(alpha = 0.08f)
                )

                1 -> listOf(
                    SplashCyan.copy(alpha = 0.02f),
                    SplashPurple.copy(alpha = 0.25f),
                    SplashPink.copy(alpha = 0.16f),
                    SplashCyan.copy(alpha = 0.0f)
                )

                else -> listOf(
                    SplashPurple.copy(alpha = 0.0f),
                    SplashCyan.copy(alpha = 0.18f),
                    SplashPurple.copy(alpha = 0.24f),
                    SplashPink.copy(alpha = 0.02f)
                )
            }

            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = waveColors,
                    startX = 0f,
                    endX = w
                )
            )

            // Fine neon edge to make the wave look luminous.
            val edgePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(-w * 0.1f, startY)

                cubicTo(
                    w * 0.2f,
                    controlY1,
                    w * 0.8f,
                    controlY2,
                    w * 1.1f,
                    endY
                )
            }

            drawPath(
                path = edgePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        SplashPink.copy(alpha = 0.05f),
                        SplashPink.copy(alpha = 0.65f),
                        SplashPurple.copy(alpha = 0.75f),
                        SplashCyan.copy(alpha = 0.7f),
                        SplashCyan.copy(alpha = 0.05f)
                    )
                ),
                style = Stroke(
                    width = (1.2f + layer * 0.35f).dp.toPx(),
                    cap = StrokeCap.Round
                ),
                alpha = 0.8f
            )
        }

        // Soft neon glow behind the logo, without circular outlines.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    SplashPurple.copy(alpha = 0.13f * pulse),
                    SplashPink.copy(alpha = 0.07f * pulse),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = w * 0.48f
            ),
            radius = w * 0.48f,
            center = Offset(centerX, centerY)
        )
    }
}


private data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val phase: Float
)

@Composable
private fun SplashParticles() {
    val particles = remember {
        List(65) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2.1f + 0.6f,
                phase = Random.nextFloat() * 6.28f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "particles")
    val twinkle by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_twinkle"
    )
    val drift by transition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_drift"
    )

    Canvas(Modifier.fillMaxSize()) {
        particles.forEachIndexed { index, particle ->
            val color = when (index % 3) {
                0 -> SplashPink
                1 -> SplashCyan
                else -> SplashPurple
            }
            val alpha = (0.18f + (index % 5) * 0.08f) * twinkle
            drawCircle(
                color = color.copy(alpha = alpha.coerceIn(0f, 0.8f)),
                radius = particle.radius,
                center = Offset(
                    x = size.width * particle.x + drift * sin(particle.phase),
                    y = size.height * particle.y
                )
            )
        }
    }
}

@Preview(
    name = "Oya Sing Neon Splash",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=852dp,dpi=440"
)
@Composable
private fun OyaSingSplashScreenPreview() {
    OyaSingSplashScreen(onFinished = {})
}
