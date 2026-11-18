
package com.karoke.myoyasing.presentation.featureUI.permissions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karoke.myoyasing.core.utils.MicrophoneCheckResult

private val MicPurple = Color(0xFFE5ACFF)
private val MicBackground = Color(0xFF17121F)
private val MicMuted = Color(0xFFC8BECF)
private val MicGreen = Color(0xFF35E58B)
private val MicRed = Color(0xFFFF6666)

@Composable
fun MicrophoneSystemCheckDialog(
    result: MicrophoneCheckResult?,
    isChecking: Boolean,
    onContinue: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val progress = remember {
        Animatable(0f)
    }

    LaunchedEffect(isChecking) {
        if (isChecking) {
            progress.snapTo(0f)

            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1800
                )
            )
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isChecking) {
                onDismiss()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MicBackground)
                .padding(26.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Microphone System Check",
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = if (isChecking) {
                    "Checking connected audio devices..."
                } else {
                    "Microphone check completed"
                },
                color = MicMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(25.dp))

            // Slim animated progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF39313F))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.value)
                        .fillMaxHeight()
                        .background(MicPurple)
                )
            }

            Spacer(Modifier.height(32.dp))

            when {
                isChecking -> {

                    CircularProgressIndicator(
                        modifier = Modifier.size(65.dp),
                        color = MicPurple,
                        strokeWidth = 4.dp
                    )

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Please wait...",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }

                result?.isExternalMicrophoneConnected == true -> {

                    // Success icon
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF153B2A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Microphone connected",
                            tint = MicGreen,
                            modifier = Modifier.size(65.dp)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "You're Good to Go!",
                        color = MicGreen,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "${result.deviceName ?: "External microphone"} connected successfully.",
                        color = MicMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(25.dp))

                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MicPurple
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Start Singing",
                            color = Color(0xFF421D50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {

                    // Warning icon
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF421C25)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "No external microphone",
                            tint = MicRed,
                            modifier = Modifier.size(65.dp)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "No External Microphone Found",
                        color = MicRed,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "For better recording quality and scoring accuracy, we recommend connecting an external microphone.",
                        color = MicMuted,
                        fontSize = 13.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Don't worry! You can still sing using your phone's built-in microphone.",
                        color = MicGreen,
                        fontSize = 12.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MicPurple
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Check Again",
                            color = Color(0xFF421D50),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(
                        onClick = onContinue
                    ) {
                        Text(
                            text = "Continue with Built-in Mic",
                            color = MicGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
