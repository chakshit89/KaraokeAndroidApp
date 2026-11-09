package com.karoke.myoyasing.presentation.feed

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import com.karoke.myoyasing.R
import com.karoke.myoyasing.core.utils.toAbsoluteUrl
import com.karoke.myoyasing.domain.model.FeedItem
import com.karoke.myoyasing.presentation.feed.viewmodel.FeedViewModel
import com.karoke.myoyasing.presentation.feed.viewmodel.PerformanceCommentsViewModel
import com.karoke.myoyasing.presentation.feed.viewmodel.PerformanceLikeUiState
import com.karoke.myoyasing.presentation.feed.viewmodel.PerformanceLikeViewModel
import com.karoke.myoyasing.presentation.feed.viewmodel.SharePerformanceViewModel
import kotlinx.coroutines.delay

import org.koin.androidx.compose.koinViewModel

private val FeedPurple = Color(0xFFE6A6FF)
private val FeedWhite = Color(0xFFF8F4FA)
private val FeedMuted = Color(0xFFB9AFBF)
private val SoraFontFamily = FontFamily(
    Font(R.font.sora_regular, FontWeight.Normal)
)

@Composable
fun OyaFeedScreen(
    currentUserId: String,
    onDiscoverClick: () -> Unit = {},
    onFeedClick: () -> Unit = {},
    onArtistLiveClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSendGiftClick: () -> Unit = {},
    viewModel: FeedViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val shareViewModel: SharePerformanceViewModel = koinViewModel()
    val likeViewModel: PerformanceLikeViewModel = koinViewModel()
    val commentsViewModel: PerformanceCommentsViewModel = koinViewModel()

    val commentsUiState by commentsViewModel.uiState.collectAsStateWithLifecycle()
//    LaunchedEffect(commentsUiState.commentAdded) {
//        if (commentsUiState.commentAdded) {
//            commentsViewModel.clearEvents()
//        }
//    }
    var selectedPerformanceId by remember {
        mutableStateOf<Long?>(null)
    }

    var selectedCommentCount by remember {
        mutableIntStateOf(0)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val likeUiState by likeViewModel.uiState.collectAsStateWithLifecycle()

    var wasLikeRequestLoading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        likeUiState.isLoading,
        likeUiState.isLiked,
        likeUiState.error
    ) {
        if (likeUiState.isLoading) {
            wasLikeRequestLoading = true
        } else if (wasLikeRequestLoading) {

            if (likeUiState.error == null) {
                val message = if (likeUiState.isLiked) {
                    "Liked successfully"
                } else {
                    "Unliked successfully"
                }

                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    context,
                    likeUiState.error,
                    Toast.LENGTH_SHORT
                ).show()
            }

            wasLikeRequestLoading = false
        }
    }
    val feedAudioPlayer = remember {
        FeedAudioPlayer(context.applicationContext)
    }

    val player = feedAudioPlayer.player

    var isPlaying by remember {
        mutableStateOf(false)
    }

    DisposableEffect(player) {

        val listener = object : Player.Listener {

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
        }
    }
    DisposableEffect(lifecycleOwner, player) {

        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_STOP) {
                feedAudioPlayer.pause()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(feedAudioPlayer) {
        onDispose {
            feedAudioPlayer.release()
        }
    }
    var selectedTab by remember { mutableStateOf("For You") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF100D15))
    ) {
        when {
            uiState.isLoading && uiState.feedItems.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = FeedPurple
                )
            }

            uiState.errorMessage != null &&
                    uiState.feedItems.isEmpty() -> {

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = uiState.errorMessage
                            ?: "Unable to load feed",
                        color = FeedWhite
                    )

                    Text(
                        text = "Tap to retry",
                        color = FeedPurple,
                        modifier = Modifier.clickable {
                            viewModel.getFeed()
                        }
                    )
                }
            }

            uiState.feedItems.isEmpty() -> {
                Text(
                    text = "No performances available yet",
                    color = FeedWhite,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                val feedItems = uiState.feedItems

                val pagerState = key(feedItems.map { it.id }) {
                    rememberPagerState(
                        initialPage = 0,
                        pageCount = { feedItems.size }
                    )
                }

                LaunchedEffect(pagerState.currentPage, feedItems) {

                    val currentItem = feedItems.getOrNull(
                        pagerState.currentPage
                    )

                    val audioUrl = currentItem?.audioUrl.toAbsoluteUrl()

                    if (!audioUrl.isNullOrBlank()) {
                        feedAudioPlayer.play(audioUrl)
                    } else {
                        feedAudioPlayer.pause()
                    }
                }


                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    key = { page -> feedItems[page].id }
                ) { page ->

                    FeedPerformancePage(
                        item = feedItems[page],
                        isPlaying = isPlaying,
                        onPlaybackToggle = {
                            feedAudioPlayer.togglePlayback()
                        },
                        onFollowClick = onFollowClick,
                        likeUiState = likeUiState,

                        onLikeClick = { performanceId, currentlyLiked ->
                            likeViewModel.toggleLike(
                                performanceId = performanceId,
                                currentlyLiked = currentlyLiked
                            )
                        },
                        onCommentClick = {
                            val currentItem = feedItems[page]
                            val performanceId = currentItem.id.toLongOrNull()

                            if (performanceId != null) {
                                selectedPerformanceId = performanceId

                                // Get initial count from Feed API
                                selectedCommentCount = currentItem.commentCount

                                commentsViewModel.loadComments(
                                    performanceId = performanceId,
                                    refresh = true
                                )
                            }

                            onCommentClick()
                        },
                        onShareClick = {

                            val currentItem = feedItems[page]

                            val performanceId = currentItem.id.toLongOrNull()
                            val audioUrl = currentItem.audioUrl.toAbsoluteUrl()

                            if (performanceId != null && !audioUrl.isNullOrBlank()) {

                                shareAudio(
                                    context = context,
                                    audioUrl = audioUrl
                                )

                                shareViewModel.sharePerformance(performanceId)

                            }
                        },
                        onSendGiftClick = onSendGiftClick
                    )
                }

                // Header remains visible while the feed swipes.
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xCC100D15),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp)
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Oya!",
                            color = Color.White,
                            fontSize = 40.sp,
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Feed",
                            color = FeedPurple,
                            fontSize = 25.sp,
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.offset(y = 10.dp)
                        )


                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = FeedWhite,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable { onSearchClick() }
                        )

                        Spacer(Modifier.width(20.dp))

                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = FeedWhite,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable { onNotificationClick() }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 12.dp,
                                start = 18.dp,
                                end = 18.dp,
                                bottom = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        listOf(
                            "For You",
                            "Trending",
                            "New",
                            "Following"
                        ).forEach { tab ->

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selectedTab == tab) {
                                            Color(0xFFC863FB)
                                        } else {

                                            FeedPurple
                                        }
                                    )
                                    .clickable {
                                        selectedTab = tab
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab,
                                    color = if (selectedTab == tab) {
                                        Color(0xFF351342)
                                    } else {
                                        Color(0xFF351342)
                                    },
                                    fontFamily = SoraFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == tab) {
                                        FontWeight.ExtraBold
                                    } else {
                                        FontWeight.Medium
                                    }
                                )
                            }
                        }
                    }
                }

                // Optional indicator showing the current position.
                Text(
                    text = "${pagerState.currentPage + 1} / ${feedItems.size}",
                    color = FeedWhite.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 111.dp, end = 20.dp)
                )

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .size(22.dp),
                        color = FeedPurple,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
        selectedPerformanceId?.let { performanceId ->

            val commentItems = remember(commentsUiState.comments) {
                commentsUiState.comments.map { it.toUi() }
            }

            PerformanceCommentsBottomSheet(
                visible = true,
                performanceId = performanceId,

                comments = commentItems,
                totalComments = selectedCommentCount,

                isLoading = commentsUiState.isLoading,
                isLoadingMore = commentsUiState.isLoadingMore,
                hasMoreComments = commentsUiState.hasMorePages,
                isSendingComment = commentsUiState.isAddingComment,

                error = commentsUiState.error,

                currentUserId = currentUserId,

                commentAdded = commentsUiState.commentAdded,
                commentDeleted = commentsUiState.commentDeleted,

                onCommentAddedHandled = {
                    selectedCommentCount++
                    commentsViewModel.clearEvents()
                },

                onCommentDeletedHandled = {
                    selectedCommentCount =
                        (selectedCommentCount - 1).coerceAtLeast(0)

                    commentsViewModel.clearEvents()
                },

                deletingCommentIds = commentsUiState.deletingCommentIds,

                onRetry = {
                    commentsViewModel.loadComments(
                        performanceId = performanceId,
                        refresh = true
                    )
                },

                onDismiss = {
                    selectedPerformanceId = null
                    commentsViewModel.clearEvents()
                },

                onSendComment = { content ->
                    commentsViewModel.addComment(
                        performanceId = performanceId,
                        content = content
                    )
                },

                onLoadMore = {
                    commentsViewModel.loadMoreComments()
                },

                onDeleteComment = { commentId ->
                    commentsViewModel.deleteComment(
                        performanceId = performanceId,
                        commentId = commentId
                    )
                }
            )
        }


    }
}

@Composable
private fun FeedPerformancePage(
    item: FeedItem,
    isPlaying: Boolean,
    onPlaybackToggle: () -> Unit,
    onFollowClick: () -> Unit,
    likeUiState: PerformanceLikeUiState,
    onLikeClick: (Long, Boolean) -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSendGiftClick: () -> Unit
) {
    var liked by remember(item.id, item.isLiked) {
        mutableStateOf(item.isLiked)
    }

    var likeCount by remember(item.id, item.likes) {
        mutableIntStateOf(item.likes)
    }

    var pendingLike by remember(item.id) {
        mutableStateOf(false)
    }
    LaunchedEffect(
        likeUiState.isLoading,
        likeUiState.isLiked,
        likeUiState.error
    ) {
        if (pendingLike && !likeUiState.isLoading) {

            if (likeUiState.error == null) {

                val newLikedState = likeUiState.isLiked

                if (newLikedState != liked) {
                    likeCount = (
                            likeCount + if (newLikedState) 1 else -1
                            ).coerceAtLeast(0)

                    liked = newLikedState
                }
            }

            pendingLike = false
        }
    }
    var showPlaybackIcon by remember(item.id) {
        mutableStateOf(true)
    }

    LaunchedEffect(isPlaying, item.id) {

        showPlaybackIcon = true

        if (isPlaying) {
            delay(650)
            showPlaybackIcon = false
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF100D15))
    ) {
        if (!item.user.profileImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = item.user.profileImageUrl,
                contentDescription = "Performance by ${item.user.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onPlaybackToggle()
                    }
            )
        } else {
            val defaultImages = listOf(
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.siz,
                R.drawable.seven,

            )

            val randomImage = remember(item.id) {
                defaultImages.random()
            }

            AsyncImage(
                model=randomImage,
                contentDescription = "Performance by ${item.user.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onPlaybackToggle()
                    }
            )
        }

        if (showPlaybackIcon) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = if (isPlaying) "Playing" else "Paused",
                        tint = Color.White,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0xA0100C18),
                            0.2f to Color.Transparent,
                            0.5f to Color.Transparent,
                            0.72f to Color(0x55100910),
                            1f to Color(0xF0100B12)
                        )
                    )
                )
        )


        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .offset(y = 35.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            val profileImageUrl = item.user.profileImageUrl

            if (!profileImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Creator profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .border(2.dp, FeedPurple, CircleShape)
                        .clickable { onFollowClick() }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.defaultprofilepic),
                    contentDescription = "Singer profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.7f),
                            CircleShape
                        )
                )
            }

            FeedAction(
                icon = if (liked) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                count = formatCount(likeCount),
                tint = if (liked) {
                    Color(0xFFFF75B8)
                } else {
                    Color.White
                },
                onClick = {

                    if (!likeUiState.isLoading && !pendingLike) {

                        val performanceId = item.id.toLongOrNull()

                        if (performanceId != null) {

                            pendingLike = true

                            onLikeClick(
                                performanceId,
                                liked
                            )
                        }
                    }
                }
            )

            FeedAction(
                icon = Icons.Default.ChatBubble,
                count = item.commentCount.toString(),
                onClick = onCommentClick
            )

            FeedAction(
                icon = Icons.Default.Share,
                count = item.shareCount.toString(),
                onClick = onShareClick
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    onSendGiftClick()
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xAA24202D))
                        .border(
                            1.dp,
                            Color(0xFF73517F),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Send gift",
                        tint = Color(0xFFFF8A58),
                        modifier = Modifier.size(35.dp)
                    )
                }

                Text(
                    text = "Send Gift",
                    color = FeedWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = SoraFontFamily,
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = 20.dp,
                    end = 82.dp,
                    bottom = 20.dp
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val profileImageUrl = item.user.profileImageUrl

                // Singer profile image
                if (!profileImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Creator profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                            .clickable { onFollowClick() }
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.defaultprofilepic),
                        contentDescription = "Singer profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.7f),
                                CircleShape
                            )
                    )
                }

                Text(
                    text = item.user.name.ifBlank { "UnKnownSinger" },
                    color = Color.White,
                    fontFamily = SoraFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )


                Box(
                    modifier = Modifier
                        .size(19.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC863FB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color(0xFF371147),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                // Follow button
                Text(
                    text = "Follow",
                    color = Color(0xFF371147),
                    fontSize = 14.sp,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFC863FB))
                        .clickable {
                            onFollowClick()
                        }
                        .padding(
                            horizontal = 18.dp,
                            vertical = 7.dp
                        )
                )
            }

            Text(
                text = "Description:  " + item.description.orEmpty(),
                color = FeedWhite,
                fontFamily = SoraFontFamily,
                fontSize = 20.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

//            Text(
//                text = buildString {
//                    append(item.song.title)
//
//                    if (item.song.artist.name.isNotBlank()) {
//                        append(" - ")
//                        append(item.song.artist.name)
//                    }
//
//                    if (item.song.artist.isVerified) {
//                        append("  ✓")
//                    }
//                },
//                color = FeedWhite,
//                fontFamily = SoraFontFamily,
//                fontWeight = FontWeight.ExtraBold,
//                fontSize = 13.sp,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color(0xCC291A20))
                    .border(
                        0.5.dp,
                        Color(0xFF60343D),
                        RoundedCornerShape(7.dp)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = FeedPurple,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = item.song.title,
                    color = Color(0xFFE3D6E3),
                    fontSize = 19.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FeedAction(
    icon: ImageVector,
    count: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = count,
            tint = tint,
            modifier = Modifier.size(35.dp)
        )

        Text(
            text = count,
            color = Color.White,
            fontFamily = SoraFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 ->
            String.format("%.1fM", count / 1_000_000.0)

        count >= 1_000 ->
            String.format("%.1fK", count / 1_000.0)

        else -> count.toString()
    }
}

private fun shareAudio(
    context: Context,
    audioUrl: String
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"

        putExtra(
            Intent.EXTRA_TEXT,
            "Listen to my music 🎵\n\n$audioUrl"
        )
    }

    val chooser = Intent.createChooser(
        shareIntent,
        "Share my Oya Sing ! karaoke"
    )

    context.startActivity(chooser)
}





