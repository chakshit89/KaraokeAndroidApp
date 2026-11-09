
package com.karoke.myoyasing.presentation.featureUI.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.karoke.myoyasing.R

private val PrepBackground = Color(0xFF080A0F)
private val PrepPurple = Color(0xFFE5ACFF)
private val PrepPurpleDark = Color(0xFF421D50)
private val PrepText = Color(0xFFF4EFF6)
private val PrepMuted = Color(0xFFC8BECF)
private val PrepBorder = Color(0xFF49344F)

private val SoraFontFamily = FontFamily(
    Font(R.font.sora_regular, FontWeight.Normal)
)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun KaraokePermissionsScreen(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onMaybeLaterClick: () -> Unit = {},
    onStartSingingClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    fun hasMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    var microphoneGranted by remember {
        mutableStateOf(hasMicrophonePermission())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        microphoneGranted = hasMicrophonePermission()

        if (!granted || !microphoneGranted) {
            Toast.makeText(
                context,
                "Please enable microphone permission from app settings.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Refresh the permission state when returning from Android Settings.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                microphoneGranted = hasMicrophonePermission()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize().navigationBarsPadding().systemBarsPadding()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF290D31),
                        Color(0xFF100D18),
                        Color(0xFF06151D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(top=10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PrepPurple,
                    modifier = Modifier
                        .size(34.dp)
                        .padding(start = 4.dp)

                        .clickable {
                            onBackClick()
                        }
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Oya Sing",
                        color = PrepPurple,
                        fontFamily = SoraFontFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

//                Icon(
//                    imageVector = Icons.Default.Settings,
//                    contentDescription = "Open app settings",
//                    tint = PrepPurple,
//                    modifier = Modifier
//                        .size(24.dp)
//                        .clickable {
//                            onSettingsClick()
//                            openAppSettings()
//                        }
//                )
            }

            Spacer(Modifier.height(76.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .height(186.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF211524),
                                Color(0xFF14232A)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        PrepBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.neon_nights
                    ),
                    contentDescription = "Neon Nights album cover",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            Color(0xFF76518B),
                            RoundedCornerShape(8.dp)
                        )
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Neon Nights",
                    color = PrepPurple,
                    fontSize = 16.sp,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text = "Starry nights and neon lights...",
                    color = PrepText,
                    fontSize = 11.sp,
                    fontFamily = SoraFontFamily,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF342039),
                                Color(0xFF151D24)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        PrepBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF472952))
                        .border(
                            1.dp,
                            Color(0xFF9463AB),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Microphone access",
                        tint = PrepPurple,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Microphone Access",
                    color = PrepText,
                    fontSize = 16.sp,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (microphoneGranted) {
                        "Microphone access granted. You're ready to sing!"
                    } else {
                        "We need microphone access to record your voice while you sing."
                    },
                    color = PrepMuted,
                    fontSize = 11.sp,
                    fontFamily = SoraFontFamily,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Grant Microphone Access
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrepPurple)
                        .clickable {
                            if (!hasMicrophonePermission()) {
                                permissionLauncher.launch(
                                    Manifest.permission.RECORD_AUDIO
                                )
                            } else {
                                microphoneGranted = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (microphoneGranted) {
                            "Access Granted ✓"
                        } else {
                            "Grant Microphone Access"
                        },
                        fontFamily = SoraFontFamily,
                        color = Color(0xFF50176A),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(26.dp))

                Text(
                    text = "Maybe Later",
                    color = PrepMuted,
                    fontSize = 14.sp,
                    fontFamily = SoraFontFamily,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onMaybeLaterClick()
                        }
                        .padding(
                            horizontal = 14.dp,
                            vertical = 4.dp
                        )
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (microphoneGranted) {
                            PrepPurple
                        } else {
                            Color(0xFF242426)
                        }
                    )
                    .clickable {
                        if (hasMicrophonePermission()) {
                            microphoneGranted = true
                            onStartSingingClick()
                        } else {
                            microphoneGranted = false

                            Toast.makeText(
                                context,
                                "Please enable microphone permission from app settings.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Start Singing",
                    color = if (microphoneGranted) {
                        Color(0xFF50176A)
                    } else {
                        Color(0xFF77717A)
                    },
                    fontFamily = SoraFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(
    name = "Karaoke Permissions Screen",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=852dp,dpi=440"
)
@Composable
fun KaraokePermissionsScreenPreview() {
    KaraokePermissionsScreen(
        onBackClick = {},
        onSettingsClick = {},
        onMaybeLaterClick = {},
        onStartSingingClick = {}
    )
}
