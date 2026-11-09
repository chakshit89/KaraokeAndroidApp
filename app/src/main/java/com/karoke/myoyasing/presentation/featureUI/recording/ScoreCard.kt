package com.karoke.myoyasing.presentation.featureUI.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreCard() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .height(76.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Color(0xCC28242B))
            .border(
                width = 1.dp,
                color = Color(0xFF5A5260),
                shape = RoundedCornerShape(11.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF264C5C)),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "★",
                color = Color(0xFF00FFFF),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "CURRENT SCORE",
                color = Color(0xFFADA4B0),
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "8,420",
                color = Color(0xFFFFFFFF),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = "PERFECT PITCH",
                color = Color(0xFFE9B3FF),
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                repeat(4) { index ->

                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(
                                if (index < 3) 19.dp
                                else 8.dp
                            )
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (index < 3)
                                    Color(0xFFE9B3FF)
                                else
                                    Color(0xFF514956)
                            )
                    )
                }
            }
        }
    }
}