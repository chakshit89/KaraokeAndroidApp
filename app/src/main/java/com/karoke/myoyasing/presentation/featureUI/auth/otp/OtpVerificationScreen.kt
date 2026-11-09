package com.karoke.myoyasing.presentation.featureUI.auth.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.karoke.myoyasing.R
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    countryCode: String = "",
    phoneNumber: String = "",
    isLoading: Boolean = false,
    error: String? = null,
    onBack: () -> Unit = {},
    onVerify: (String) -> Unit = {},
    onEditPhone: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onResendOtp: () -> Unit = {}
) {

    var otp by remember {
        mutableStateOf("")
    }

    var remainingSeconds by remember {
        mutableStateOf(38)
    }

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
    }

    val backgroundColor = Color(0xFF121212)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF61227A).copy(alpha = 0.75f),
                            Color(0xFF3B164B).copy(alpha = 0.60f),
                            Color(0xFF121212).copy(alpha = 0f)
                        ),
                        center = Offset(
                            x = 195f,
                            y = 450f
                        ),
                        radius = 430f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color(0xFF121212))
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFE8B2FF),
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onBack()
                        }
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Text(
                    text = stringResource(R.string.verify),
                    color = Color(0xFFE4B0FA),
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.sora_bold) ),

                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF5A236D))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(
                    modifier = Modifier.height(118.dp)
                )


                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF9C5AB5).copy(alpha = 0.65f),
                            shape = CircleShape
                        )
                        .background(
                            Color(0xFF6E347D).copy(alpha = 0.22f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Verification",
                        tint = Color(0xFFE2A4FF),
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(25.dp)
                )


                Text(
                    text = stringResource(R.string.otp_verification),
                    color = Color(0xFFF1EAF2),
                    fontSize = 34.sp,
                    fontFamily = FontFamily(Font(R.font.sora_bold) ),
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = stringResource(R.string.verification_subtitle),
                    color = Color(0xFFD1C3D4),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.sora_medium) ),
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF111111))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "$countryCode $phoneNumber",
                        color = Color(0xFFE6E1E7),
                        fontFamily = FontFamily(Font(R.font.sora_bold)),
                        fontSize = 14.sp
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )


                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit number",
                        tint = Color(0xFFD99AF2),
                        modifier = Modifier
                            .size(17.dp)
                            .clickable {
                                onEditPhone()
                            }
                    )

                }

                Spacer(
                    modifier = Modifier.height(40.dp)
                )


                OtpInput (
                    otp = otp,
                    onOtpChange = {
                        otp = it
                    }
                )

                Spacer(
                    modifier = Modifier.height(25.dp)
                )


                Text(
                    text = if (remainingSeconds > 0) {
                        "Resend in 00:${remainingSeconds.toString().padStart(2, '0')}"
                    } else {
                        "Resend"
                    },
                    color = Color(0xFFD5C8D8),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(
                        enabled = remainingSeconds == 0 && !isLoading
                    ) {
                        onResendOtp()
                        remainingSeconds = 38
                    }
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                if (error != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = error,
                        color = Color(0xFFFF7777),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                Button(
                    onClick = {
                        if (otp.length == 4) {
                            onVerify(otp)
                        }
                    },
                    enabled = !isLoading && otp.length == 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(57.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0A2FA),
                        disabledContainerColor = Color(0xFF8B6B96)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(
                        text = if (isLoading) {
                            "Verifying..."
                        } else {
                            stringResource(R.string.verify_continue)
                        },
                        color = Color(0xFF4D155F),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.sora_bold))
                    )
                }

                Spacer(
                    modifier = Modifier.height(44.dp)
                )

                /*
                 * SUPPORT
                 */
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Didn't receive the code? ",
                        color = Color(0xFFD2C8D4),
                        fontFamily = FontFamily(Font(R.font.sora_thin) ),
                        fontSize = 10.sp
                    )

                    Text(
                        text = "Contact Support",
                        color = Color(0xFFE1A5F7),
                        fontSize = 10.sp,
                        fontFamily = FontFamily(Font(R.font.sora_semiblold) ),
                        modifier = Modifier.clickable {
                            onContactSupport()
                        }
                    )
                }
            }
        }
    }
}
@Preview(
    showBackground = true,
    showSystemUi = true,

)
@Composable
private fun OtpScreenPreview() {
  OtpVerificationScreen()
}
@Composable
private fun OtpInput(
    otp: String,
    onOtpChange: (String) -> Unit
) {

    BasicTextField(
        value = otp,
        onValueChange = { value ->
            onOtpChange(
                value
                    .filter { it.isDigit() }
                    .take(4)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        cursorBrush = SolidColor(Color.Transparent),
        textStyle = TextStyle(
            color = Color.Transparent
        ),
        modifier = Modifier
            .width(304.dp)
            .height(65.dp),
        decorationBox = {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                repeat(4) { index ->

                    val isFocused = index == otp.length.coerceAtMost(3)
                    val character =
                        otp.getOrNull(index)?.toString() ?: "-"

                    Box(
                        modifier = Modifier
                            .size(
                                width = 60.dp,
                                height = 45.dp
                            )
                            .background(
                                Color.White,
                                RoundedCornerShape(0.dp)
                            )
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) {
                                    Color(0xFF2969FF)
                                } else {
                                    Color(0xFFB7B7B7)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = character,
                            color = if (character == "-") {
                                Color(0xFF777777)
                            } else {
                                Color(0xFF222222)
                            },
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}