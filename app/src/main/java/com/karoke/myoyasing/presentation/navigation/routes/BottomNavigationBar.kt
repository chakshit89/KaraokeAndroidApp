package com.karoke.myoyasing.presentation.navigation.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onFeedClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val items = listOf(
        BottomNavItem(
            title = "Home",
            icon = Icons.Outlined.Home,
            onClick = onHomeClick
        ),
        BottomNavItem(
            title = "Feed",
            icon = Icons.Outlined.VideoLibrary,
            onClick = onFeedClick
        ),
        BottomNavItem(
            title = "Profile",
            icon = Icons.Outlined.Person,
            onClick = onProfileClick
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Color(0xFF1B191B))
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val selected = selectedTab == index

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { item.onClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(43.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            if (selected) {
                                Color(0xFF6F287E)
                            } else {
                                Color.Transparent
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selected) {
                            Color(0xFFF3B6FF)
                        } else {
                            Color(0xFF8D858F)
                        },
                        modifier = Modifier.size(23.dp)
                    )
                }

                Text(
                    text = item.title,
                    color = if (selected) {
                        Color(0xFFF3B6FF)
                    } else {
                        Color(0xFF8D858F)
                    },
                    fontSize = 12.sp
                )
            }
        }
    }
}

private data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)