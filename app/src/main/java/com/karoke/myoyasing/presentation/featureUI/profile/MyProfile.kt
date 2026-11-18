package com.karoke.myoyasing.presentation.featureUI.profile


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karoke.myoyasing.R

@Composable
fun MyProfile() {
    val cardBg = Color(0xFF121216)
    val surfaceDark = Color(0xFF1C1C22)
    val buttonDark = Color(0xFF26262E)
    val neonPurple = Color(0xFFD86BFF)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
                , tint = Color.White
            )


            Text(
                text = "My Profile",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(cardBg)
                .drawBehind {
                    // Top-left purple ambient glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6B21A8).copy(alpha = 0.45f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.2f, size.height * 0.08f),
                            radius = size.width * 0.65f
                        ),
                        radius = size.width * 0.65f,
                        center = Offset(size.width * 0.2f, size.height * 0.08f)
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF0E7490).copy(alpha = 0.35f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.85f, size.height * 0.15f),
                            radius = size.width * 0.6f
                        ),
                        radius = size.width * 0.6f,
                        center = Offset(size.width * 0.85f, size.height * 0.15f)
                    )
                }
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Overlapping Avatar + Mic Badge
                Box(modifier = Modifier.size(104.dp)) {
                    // Replace this Box with your Image(...) composable
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF2D2D3A))
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFFC084FC), Color(0xFF38BDF8))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    // Bottom-Right Mic Badge
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFE879F9), Color(0xFF9333EA))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Singer Badge",
                            tint = Color(0xFF1E1028),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Name & Verified Tick
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Aria Chen",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 3. Username Handle
                Text(
                    text = "@aria_singz",
                    color = Color(0xFFC084FC),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Bio Text
                Text(
                    text = "Karaoke lover 🎤 • High notes & late\nnight vibes • Singing live every evening\n✨",
                    color = Color(0xFFD1D5DB),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Genre Tags Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GenreChip(text = "#SYNTHPOP", textColor = Color(0xFF38BDF8))
                    GenreChip(text = "#RNB", textColor = Color(0xFFC084FC))
                    GenreChip(text = "#BELTER", textColor = Color(0xFFFB7185))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6. Stats Container
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(surfaceDark.copy(alpha = 0.85f))
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(
                        value = "12.4K",
                        label = "FOLLOWERS",
                        valueColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        value = "430",
                        label = "FOLLOWING",
                        valueColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        value = "58",
                        label = "RECORDINGS",
                        valueColor = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 7. Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit Profile Button
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonDark),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profile",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Glowing Sing Now Button
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = neonPurple),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .drawBehind {
                                drawRoundRect(
                                    color = neonPurple.copy(alpha = 0.45f),
                                    size = size
                                )
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Sing",
                            tint = Color(0xFF1A0926),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sing Now",
                            color = Color(0xFF1A0926),
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun GenreChip(
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF23232B))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    valueColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = valueColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp
        )
    }
}

@Preview(
    name = "MyProfile Screen",
    showBackground = true,
    backgroundColor = 0xFF09090B,
    showSystemUi = true
)
@Composable
fun MyProfilePreview() {
    MyProfile()
}
