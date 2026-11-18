package com.karoke.myoyasing.presentation.featureUI.home

import android.app.Activity
import android.graphics.Color.alpha
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.karoke.myoyasing.R

import com.karoke.myoyasing.domain.model.Song
import com.karoke.myoyasing.presentation.featureUI.Homesong.MiniPlayerDialog
import com.karoke.myoyasing.presentation.featureUI.Homesong.SongPreviewController
import com.karoke.myoyasing.presentation.featureUI.Homesong.rememberSongPreviewController
import com.karoke.myoyasing.presentation.featureUI.recording.SongDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue

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
    val context = LocalContext.current
    val activity = context as? Activity

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var backPressedOnce by remember {
        mutableStateOf(false)
    }
    BackHandler {

        if (backPressedOnce) {
            activity?.finish()
        } else {

            backPressedOnce = true

            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Press back again to exit the application"
                )
            }

            scope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }
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
    var selectedLyrics by remember {
        mutableStateOf("")
    }

    var showSongChooser by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        containerColor = Background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
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
                    showSongChooser = true
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
                            selectedLyrics = song.lyricsUrl
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
                    lyricsUrl = selectedLyrics,
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

    if (showSongChooser) {

        ChooseSongDialog(
            songs = trendingSongs,

            onSongSelected = { song ->

                showSongChooser = false

                onSingNowClick(song)
            },

            onDismiss = {
                showSongChooser = false
            }
        )
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

//        Box(
//            modifier = Modifier
//                .size(38.dp)
//                .clickable(onClick = onNotificationClick),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = Icons.Outlined.NotificationsNone,
//                contentDescription = "Notifications",
//                tint = White,
//                modifier = Modifier.size(24.dp)
//            )
//
//            Box(
//                modifier = Modifier
//                    .size(17.dp)
//                    .offset(x = 8.dp, y = (-8).dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFFFF4141)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "3",
//                    color = Color.White,
//                    fontSize = 9.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }

//        Spacer(modifier = Modifier.width(6.dp))
//
//        IconButton(onClick = onSearchClick) {
//            Icon(
//                imageVector = Icons.Default.Search,
//                contentDescription = "Search",
//                tint = White,
//                modifier = Modifier.size(26.dp)
//            )
//        }
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
        val currentContent = heroContents[currentImageIndex % heroContents.size]

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.62f)
                .padding(start = 20.dp, top = 18.dp, bottom = 18.dp)
        ) {
            AnimatedContent(
                targetState = currentContent,
                transitionSpec = {
                    fadeIn(tween(600)) togetherWith fadeOut(tween(600))
                },
                label = "heroTextTransition"
            ) { content ->
                Column {
                    Text(
                        text = content.topLabel,
                        color = Color(0xFFB9AEBB),
                        fontFamily = SoraFontFamily,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = content.titleLine1,
                        color = Color.White,
                        fontSize = 30.sp,
                        fontFamily = SoraFontFamily,
                        lineHeight = 35.sp
                    )
                    Text(
                        text = content.titleLine2,
                        color = Purple,
                        fontSize = 30.sp,
                        fontFamily = SoraFontFamily,
                        lineHeight = 35.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = content.subtitle,
                        color = Color(0xFFACA4AE),
                        fontSize = 11.sp,
                        fontFamily = SoraFontFamily,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            NeonSingButton(
                onSingNowClick = onSingNowClick,
                isSongAvailable = isSongAvailable
            )

//            Button(
//                onClick = onSingNowClick,
//                enabled = isSongAvailable,
//                modifier = Modifier
//                    .width(130.dp)
//                    .height(42.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF202020),
//                    disabledContainerColor = Color(0xFF454545)
//                ),
//                contentPadding = PaddingValues(
//                    horizontal = 8.dp,
//                    vertical = 0.dp
//                )
//            ) {
//
//                Icon(
//                    imageVector = Icons.Default.Mic,
//                    contentDescription = null,
//                    modifier = Modifier.size(15.dp),
//                    tint = Color.White
//                )
//
//                Spacer(modifier = Modifier.width(7.dp))
//
//                Text(
//                    text = "Sing Now",
//                    color = Color.White,
//                    fontFamily = SoraFontFamily,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 12.sp,
//                    maxLines = 1
//                )
//            }
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
) {
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

        val context = LocalContext.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = song.artist ?: "Unknown artist",
                modifier = Modifier.weight(1f),
                color = Color(0xFFAFA8B0),
                fontSize = 11.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "This functionality is coming soon!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier.height(34.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 0.dp
                )
            ) {
                Text(
                    text = "Set RBT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

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

@Composable
fun ChooseSongDialog(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    onDismiss: () -> Unit
) {
    if (songs.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { songs.size }
    )
    val currentSong = songs[pagerState.currentPage]

    val infiniteTransition = rememberInfiniteTransition(label = "neon")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glow"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2A1A2E), Color(0xFF121012))
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFCE5BFF).copy(0.8f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(top = 16.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // TOP CENTER TITLE - Fixed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Choose a Song",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(32.dp)
                            .background(Color.White.copy(0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // TIGHTER, BIGGER PAGER
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 62.dp), // reduced from 85
                    pageSpacing = 8.dp, // reduced from 16
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // increased from 260
                ) { page ->
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    val scale = lerp(0.82f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                    val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                    val song = songs[page]
                    val isCurrent = page == pagerState.currentPage

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Neon glow behind selected image
                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(215.dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    Color(0xFFE94DFF).copy(alpha = 0.4f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                        .blur(20.dp)
                                )
                            }
                            AsyncImage(
                                model = song.coverImageUrl,
                                contentDescription = song.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(210.dp) // increased from 185
                                    .shadow(
                                        if (isCurrent) 30.dp else 8.dp,
                                        RoundedCornerShape(28.dp),
                                        spotColor = Color(0xFFE94DFF).copy(0.6f)
                                    )
                                    .clip(RoundedCornerShape(28.dp))
                                    .border(
                                        if (isCurrent) 2.dp else 0.dp,
                                        if (isCurrent) Color(0xFFE94DFF).copy(0.6f) else Color.Transparent,
                                        RoundedCornerShape(28.dp)
                                    )
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = song.title,
                            color = if (isCurrent) Color.White else Color.White.copy(0.6f),
                            fontSize = 15.sp,
                            fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            modifier = Modifier.alpha(if (isCurrent) 0f else 1f) // hide inside pager, show below
                        )
                    }
                }

                // Dots indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    repeat(songs.size) { index ->
                        val isSelected = index == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 22.dp else 6.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color(0xFFE94DFF) else Color.White.copy(0.2f)
                                )
                                .animateContentSize()
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = currentSong.title,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Text(
                    text = currentSong.artist ?: "Trending • Karaoke",
                    color = Color.White.copy(0.5f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(28.dp))

                // NEONIC SING NOW BUTTON
                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .width(180.dp)
                        .shadow(20.dp, CircleShape, spotColor = Color(0xFFE94DFF))
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFF5CFF), Color(0xFF9E1DD9))
                            )
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = glowAlpha * 0.6f),
                            CircleShape
                        )
                        .clickable { onSongSelected(currentSong) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Sing Now",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.White.copy(alpha = 0.8f),
                                    blurRadius = 12f
                                )
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
private fun lerp(
    start: Float,
    stop: Float,
    fraction: Float
): Float {

    return start +
            (stop - start) *
            fraction
}

data class HeroContent(
    val topLabel: String,
    val titleLine1: String,
    val titleLine2: String,
    val subtitle: String
)

private val heroContents = listOf(
    HeroContent("Sing it. Share it.", "Own the", "Stage!", "Record, perform and shine\nwith the world."),
    HeroContent("Find Your Voice", "Feel the", "Beat!", "Thousands of tracks\nwaiting for you."),
    HeroContent("Your Moment", "Unleash", "Your Star!", "From bathroom singer to\nstage performer."),
    HeroContent("Karaoke Night?", "Hit Every", "High Note!", "Perfect pitch correction\n& studio effects."),
    HeroContent("Duet Ready?", "Sing With", "Friends!", "Invite friends and create\nviral duets."),
    HeroContent("No Crowd Needed", "Your Stage,", "Your Rules!", "Record anytime, anywhere\nand go viral."),
    HeroContent("Challenge Time", "Battle &", "Shine!", "Join singing battles and\ntop the charts."),
    HeroContent("Love to Sing?", "Express Your", "Soul!", "Turn your feelings into\nmusic that speaks."),
    HeroContent("Just For Fun", "Sing Loud,", "Sing Proud!", "No judgment, just pure\nkaraoke joy.")
)

@Composable
fun NeonSingButton(
    onSingNowClick: () -> Unit,
    isSongAvailable: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neonBorder")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val borderBrush = Brush.sweepGradient(
        colors = listOf(
            Color(0xFF9C27B0),
            Color(0xFFE040FB),
            Color(0xFF7C4DFF),
            Color(0xFF9C27B0)
        )
    )

    // FIXED: No more Brush.copy() and no blur()
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(42.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFFB537F2),
                ambientColor = Color(0xFFB537F2)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .drawWithContent {
                rotate(rotation) {
                    drawCircle(
                        brush = borderBrush,
                        style = Stroke(width = 6.dp.toPx())
                    )
                }
                drawContent()
            }
            .padding(1.5.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF202020), Color(0xFF121212))
                )
            )
            .clickable(enabled = isSongAvailable) { onSingNowClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFE040FB), Color(0xFF7C4DFF))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sing Now",
                color = Color.White,
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp
            )
        }
    }
}
