
package com.karoke.myoyasing.presentation.featureUI.home

import android.graphics.Color.alpha
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.karoke.myoyasing.R
import com.karoke.myoyasing.domain.model.Song
import com.karoke.myoyasing.presentation.featureUI.Homesong.MiniPlayerDialog
import com.karoke.myoyasing.presentation.featureUI.Homesong.SongPreviewController
import com.karoke.myoyasing.presentation.featureUI.Homesong.rememberSongPreviewController
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private val Background = Color(0xFF111111)
private val Purple = Color(0xFFD875FF)
private val PurpleDark = Color(0xFF7B20A5)
private val White = Color(0xFFF7F4F7)
private val Gray = Color(0xFFA8A1AA)

private val SoraFontFamily = FontFamily(
    Font(R.font.sora_regular, FontWeight.Normal)
)

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    onSingNowClick: (Song) -> Unit = {},
    onSongClick: (Song) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    viewModel: SongsViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverContent(
        modifier = modifier,
        uiState = uiState,
        onRetryClick = viewModel::loadSongs,
        onSingNowClick = onSingNowClick,
        onSongClick = onSongClick,
        onSearchClick = onSearchClick,
        onNotificationClick = onNotificationClick
    )
}

@Composable
private fun DiscoverContent(
    modifier: Modifier = Modifier,
    uiState: SongsUiState,
    onRetryClick: () -> Unit,
    onSingNowClick: (Song) -> Unit,
    onSongClick: (Song) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val songPreviewController = rememberSongPreviewController()
    val trendingSongs = uiState.songs

    val newReleaseSongs = remember(uiState.songs) {
        uiState.songs.shuffled()
    }
    var showMiniPlayer by remember {
        mutableStateOf(false)
    }

    var selectedSongTitle by remember {
        mutableStateOf("")
    }

    var selectedArtistName by remember {
        mutableStateOf("")
    }

    var selectedThumbnail by remember {
        mutableStateOf<Painter?>(null)
    }
    var selectedAudioUrl by remember {
        mutableStateOf("")
    }
    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = Background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(5.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        )
        {
            TopHeader(
                onSearchClick = onSearchClick,
                onNotificationClick = onNotificationClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            HeroBanner(
                onSingNowClick = {
                    trendingSongs.firstOrNull()?.let(onSingNowClick)
                },
                isSongAvailable = trendingSongs.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoading && uiState.songs.isEmpty() -> {
                    LoadingSongs()
                }

                uiState.errorMessage != null &&
                        uiState.songs.isEmpty() -> {
                    SongsError(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick
                    )
                }

                uiState.songs.isEmpty() -> {
                    EmptySongs()
                }

                else -> {
                    SectionHeader(
                        title = "Trending on Oya!Feed"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SongHorizontalList(
                        songs = trendingSongs,
                        showLikes = true,
                        songPreviewController = songPreviewController,
                        onSongClick = onSongClick,
                        onPlaySong = { song, thumbnailPainter ->


                            selectedSongTitle = song.title
                            selectedArtistName = song.artist ?: "Unknown artist"
                            selectedThumbnail = thumbnailPainter
                            selectedAudioUrl = song.audioUrl

                            showMiniPlayer = true

                            if (
                                songPreviewController.currentSongId != song.id ||
                                !songPreviewController.isPlaying
                            ) {
                                songPreviewController.togglePlayback(
                                    songId = song.id,
                                    audioUrl = song.audioUrl
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    SectionHeader(
                        title = "New Releases"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SongHorizontalList(
                        songs = newReleaseSongs,
                        showLikes = false,
                        songPreviewController = songPreviewController,
                        onSongClick = onSongClick,
                        onPlaySong = { song, thumbnailPainter ->

                            // Open the mini-player with the selected song.
                            selectedSongTitle = song.title
                            selectedArtistName = song.artist ?: "Unknown artist"
                            selectedThumbnail = thumbnailPainter
                            selectedAudioUrl = song.audioUrl

                            showMiniPlayer = true

                            if (
                                songPreviewController.currentSongId != song.id ||
                                !songPreviewController.isPlaying
                            ) {
                                songPreviewController.togglePlayback(
                                    songId = song.id,
                                    audioUrl = song.audioUrl
                                )
                            }
                        }
                    )
                }
            }
            if (showMiniPlayer && selectedThumbnail != null) {
                MiniPlayerDialog(
                    controller = songPreviewController,
                    songTitle = selectedSongTitle,
                    artistName = selectedArtistName,
                    thumbnail = selectedThumbnail!!,
                    audioUrl = selectedAudioUrl,
                    onDismiss = {

                        songPreviewController.stop()

                        showMiniPlayer = false

                        selectedSongTitle = ""
                        selectedArtistName = ""
                        selectedThumbnail = null
                        selectedAudioUrl = ""
                    }
                )
            }
            Spacer(modifier = Modifier.height(28.dp))

//            JoinImproveCard(
//                onClick = {}
//            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopHeader(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Oya!",
                color = White,
                fontSize = 45.sp,
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.Bold
            )


            Text(
                text = "Sing",
                color = Purple,
                fontSize = 25.sp,
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.offset(y = 20.dp)
            )

        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clickable(onClick = onNotificationClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = "Notifications",
                tint = White,
                modifier = Modifier.size(24.dp)
            )

            Box(
                modifier = Modifier
                    .size(17.dp)
                    .offset(x = 8.dp, y = (-8).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF4141)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun HeroBanner(
    onSingNowClick: () -> Unit,
    isSongAvailable: Boolean
) {
    val bannerImages = listOf(
        R.drawable.homescreenthemeicon,
        R.drawable.hero_banner_1,
        R.drawable.hero_banner_2,
        R.drawable.hero_banner_3,
        R.drawable.hero_banner_4,
        R.drawable.hero_banner_5,
        R.drawable.hero_banner_6,
        R.drawable.hero_banner_7,
        R.drawable.hero_banner_8
    )

    var currentImageIndex by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            currentImageIndex =
                (currentImageIndex + 1) % bannerImages.size
        }
    }

    val cardBackground = Color(0xFF171419)

    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(cardBackground)
    ) {

        // Base background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBackground)
        )

        // RIGHT SIDE: Rotating background images
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.62f)
                .align(Alignment.CenterEnd)
        ) {
            Crossfade(
                targetState = currentImageIndex,
                animationSpec = tween(durationMillis = 700),
                label = "heroBannerTransition"
            ) { index ->

                Image(
                    painter = painterResource(
                        id = bannerImages[index]
                    ),
                    contentDescription = "Concert banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // HORIZONTAL FADE:
        // Smoothly blends the image into the text area.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.0f to cardBackground,
                            0.25f to cardBackground,
                            0.40f to cardBackground.copy(alpha = 0.96f),
                            0.52f to cardBackground.copy(alpha = 0.78f),
                            0.64f to cardBackground.copy(alpha = 0.42f),
                            0.76f to cardBackground.copy(alpha = 0.12f),
                            0.88f to Color.Transparent,
                            1.0f to Color.Transparent
                        )
                    )
                )
        )

        // VERTICAL FADE:
        // Blends the bottom of the image into the card.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.50f to Color.Transparent,
                            0.70f to cardBackground.copy(alpha = 0.12f),
                            0.85f to cardBackground.copy(alpha = 0.48f),
                            1.0f to cardBackground.copy(alpha = 0.90f)
                        )
                    )
                )
        )

        // LEFT SIDE: Text and button remain stationary.
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.62f)
                .padding(
                    start = 20.dp,
                    top = 18.dp,
                    bottom = 18.dp
                )
        ) {

            Text(
                text = "Sing it. Share it.",
                color = Color(0xFFB9AEBB),
                fontFamily = SoraFontFamily,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Own the",
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = SoraFontFamily,
                lineHeight = 35.sp
            )

            Text(
                text = "Stage!",
                color = Purple,
                fontSize = 30.sp,
                fontFamily = SoraFontFamily,
                lineHeight = 35.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Record, perform and shine\nwith the world.",
                color = Color(0xFFACA4AE),
                fontSize = 11.sp,
                fontFamily = SoraFontFamily,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSingNowClick,
                enabled = isSongAvailable,
                modifier = Modifier
                    .width(130.dp)
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF202020),
                    disabledContainerColor = Color(0xFF454545)
                ),
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 0.dp
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = "Sing Now",
                    color = Color.White,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}


@Composable
private fun SectionHeader(
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = White,
            fontSize = 20.sp,
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun SongHorizontalList(
    songs: List<Song>,
    showLikes: Boolean,
    songPreviewController: SongPreviewController,
    onSongClick: (Song) -> Unit,
    onPlaySong: (Song, Painter) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = songs,
            key = { it.id }
        ) { song ->

            SongCard(
                song = song,
                showLikes = showLikes,
                controller = songPreviewController,
                onClick = { onSongClick(song) },
                onPlaySong = onPlaySong
            )
        }
    }
}

@Composable
private fun SongCard(
    song: Song,
    showLikes: Boolean,
    controller: SongPreviewController,
    onClick: () -> Unit,
    onPlaySong: (Song, Painter) -> Unit
)  {
    val isCurrentSong = controller.currentSongId == song.id
    val isPlaying = isCurrentSong && controller.isPlaying

    val songThumbnailPainter = rememberAsyncImagePainter(
        model = song.coverImageUrl
    )


    Column(
        modifier = Modifier
            .width(170.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            var isImageLoaded by remember(song.coverImageUrl) {
                mutableStateOf(false)
            }
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.coverImageUrl)
                    .memoryCacheKey(song.coverImageUrl)
                    .diskCacheKey(song.coverImageUrl)
                    .crossfade(200)
                    .build(),
                contentDescription = song.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF25212A)),
                        contentAlignment = Alignment.Center
                    ) {
                        ShimmerImagePlaceholder(
                            modifier = Modifier.size(70.dp)
                        )

                        Icon(
                            painter = painterResource(R.drawable.mic),
                            contentDescription = null,
                            tint = Color(0xFFD946FF),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF25212A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Image unavailable",
                            tint = Color(0xFF8D8294),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Microphone",
                    tint = Color.White,
                    modifier = Modifier.size(23.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0xAA000000)
                            )
                        )
                    )
            )


            if (song.duration > 0) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Text(
                        text = formatDuration(song.duration),
                        color = Color.White,
                        fontFamily = SoraFontFamily,
                        fontSize = 11.sp
                    )
                }
            }


            IconButton(
                onClick = {
                    onPlaySong(
                        song,
                        songThumbnailPainter
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFFD946FF).copy(alpha = 0.45f)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Open player for ${song.title}",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            color = White,
            fontSize = 16.sp,
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )

        Text(
            text = song.artist ?: "Unknown artist",
            color = Color(0xFFAFA8B0),
            fontSize = 11.sp,
            maxLines = 1
        )

        if (showLikes) {
            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFFF5360),
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = formatLikes(song.likes),
                    color = Color(0xFFAAA4AA),
                    fontSize = 10.sp
                )
            }
        }
    }
}

private fun formatDuration(durationSeconds: Int): String {
    return "%d:%02d".format(
        durationSeconds / 60,
        durationSeconds % 60
    )
}

private fun formatLikes(likes: Int): String {
    return when {
        likes >= 1_000_000 ->
            "%.1fM".format(likes / 1_000_000f)

        likes >= 1_000 ->
            "%.1fK".format(likes / 1_000f)

        else -> likes.toString()
    }
}

@Composable
private fun LoadingSongs() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Purple)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Loading songs...",
            color = Gray,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ShimmerImagePlaceholder(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "cover_shimmer")

    val shimmerOffset by transition.animateFloat(
        initialValue = -350f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF25212A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF25212A),
                            Color(0xFF403649),
                            Color(0xFF65506D),
                            Color(0xFF403649),
                            Color(0xFF25212A)
                        ),
                        start = Offset(shimmerOffset, 0f),
                        end = Offset(shimmerOffset + 250f, 250f),
                        tileMode = TileMode.Clamp
                    )
                )
        )
    }
}

@Composable
private fun SongsError(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = Color(0xFFFF7777),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleDark
            )
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptySongs() {
    Text(
        text = "No songs available right now.",
        color = Gray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    )
}

@Composable
private fun JoinImproveCard(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(Color(0xFF252323))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color(0xFF4C3A52)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(modifier = Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Join and Improve",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "your singing Skills",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Sing with your favorite\nartists and win rewards!",
                color = Color(0xFFAAA4AA),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }

        Button(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B383B)
            ),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            Text(
                text = "Join Now",
                color = White,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
