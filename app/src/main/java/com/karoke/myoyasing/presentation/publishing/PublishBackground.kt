package com.karoke.myoyasing.presentation.publishing

import android.R.attr.font
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.karoke.myoyasing.R
import com.karoke.myoyasing.core.utils.MediaUrlHelper
import com.karoke.myoyasing.core.utils.NeonEqualizer
import com.karoke.myoyasing.presentation.navigation.routes.AppRoutes
import com.karoke.myoyasing.presentation.publishing.viewmodels.HashtagUiState
import com.karoke.myoyasing.presentation.publishing.viewmodels.HashtagViewModel
import com.karoke.myoyasing.presentation.publishing.viewmodels.PublishPerformanceViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File

private val PublishBackground = Color(0xFF121212)
private val PublishSurface = Color(0xFF202020)
private val PublishBorder = Color(0xFF413343)
private val PublishPurple = Color(0xFFE7A9FF)
private val PublishText = Color(0xFFE8E2E9)
private val PublishMuted = Color(0xFFBDB3C0)
private val SoraFontFamily = FontFamily(
    Font(R.font.sora_regular, FontWeight.Normal)
)

@Composable
fun PublishPerformanceScreen(
    navController: NavController,
    songTitle: String,
    duration: String = "03:45",

    performanceId: Int? = null,
    recordingPath: String? = null,
    audioUrl: String? = null,
    thumbnailUrl: String? = null,
    videoUrl: String? = null,

    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onPreviewClick: () -> Unit = {},
    onCollaboratorsClick: () -> Unit = {},
    onAudienceClick: () -> Unit = {},
    onPublishClick: (String) -> Unit = {},
    hashtagViewModel: HashtagViewModel = koinViewModel(),
    publishPerformanceViewModel: PublishPerformanceViewModel =
        koinViewModel()
) {

    BackHandler(
        enabled = true
    ) {
        onBackClick()
    }
    var description by remember {
        mutableStateOf("")
    }

    var editableSongTitle by remember(songTitle) {
        mutableStateOf(songTitle)
    }
    var selectedAudience by rememberSaveable {
        mutableStateOf("PUBLIC")
    }

    var audienceMenuExpanded by remember {
        mutableStateOf(false)
    }

    val selectedHashtagIds = remember {
        mutableStateListOf<Int>()
    }

    val hashtagUiState by hashtagViewModel.uiState.collectAsState()

    val activeHashtagQuery = remember(description) {
        extractActiveHashtag(description)
    }
    val publishUiState by publishPerformanceViewModel.uiState
        .collectAsState()

    val context = LocalContext.current


    LaunchedEffect(publishUiState.isPublished) {
        if (publishUiState.isPublished) {
            navController.navigate(AppRoutes.FEED) {
                popUpTo(AppRoutes.PUBLISHPERFORMANCE) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    LaunchedEffect(
        publishUiState.message,
        publishUiState.error
    ) {
        publishUiState.message?.let { message ->
            if (publishUiState.isPublished) {
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()

                publishPerformanceViewModel.clearMessage()
            }
        }

        publishUiState.error?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()

            publishPerformanceViewModel.clearError()
        }
    }

    LaunchedEffect(activeHashtagQuery) {
        if (activeHashtagQuery != null) {
            hashtagViewModel.onHashtagQueryChanged(
                activeHashtagQuery
            )
        } else {
            hashtagViewModel.clearSearch()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PublishBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .systemBarsPadding()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFF292329)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Back arrow aligned to the left.
                    IconButton(
                        onClick =  { onBackClick() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 6.dp)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to preview",
                            tint = PublishPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Title centered relative to the entire screen.
                    Text(
                        text = "Publish",
                        color = PublishPurple,
                        fontFamily = FontFamily(
                            Font(R.font.sora_extrabold)
                        ),
                        textAlign = TextAlign.Center,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }



            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp)
                    .padding(top = 32.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                VideoPreviewCard(
                    duration = duration,
                    thumbnailUrl = thumbnailUrl,
                    audioUrl = audioUrl,
                    recordingPath = recordingPath,
                    onClick = onPreviewClick
                )

                Spacer(Modifier.height(30.dp))

                SectionLabel("DESCRIPTION")

                Spacer(Modifier.height(7.dp))

                DescriptionCard(
                    description = description,

                    onDescriptionChange = { newDescription ->
                        description = newDescription
                    },

                    hashtags = hashtagUiState.hashtags,
                    isLoading = hashtagUiState.isLoading,
                    noResults = hashtagUiState.noResults,
                    error = hashtagUiState.error,
                    isSearching = activeHashtagQuery != null,
                onHashtagClick = { hashtag ->
                    description = insertSelectedHashtag(
                        description = description,
                        hashtag = hashtag.name
                    )

                    val hashtagId = hashtag.id.toString().toIntOrNull()

                    if (hashtagId != null && hashtagId !in selectedHashtagIds) {
                        selectedHashtagIds.add(hashtagId)
                    }

                    hashtagViewModel.clearSearch()
                }
                )

                Spacer(Modifier.height(23.dp))

                SectionLabel("SONG TITLE")

                Spacer(Modifier.height(7.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(59.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(PublishSurface)
                        .padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = PublishPurple,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(Modifier.width(18.dp))

                    BasicTextField(
                        value = editableSongTitle,
                        onValueChange = { editableSongTitle = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = PublishText,
                            fontSize = 16.sp,
                            fontFamily = SoraFontFamily
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (editableSongTitle.isEmpty()) {
                                    Text(
                                        text = "Enter song title...",
                                        color = Color(0xFF777078),
                                        fontSize = 16.sp
                                    )
                                }

                                innerTextField()
                            }
                        }
                    )
                }


                Spacer(Modifier.height(23.dp))

                SectionLabel("COLLABORATORS")

                Spacer(Modifier.height(7.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(PublishSurface)
                        .clickable { onCollaboratorsClick() }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = PublishMuted,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(Modifier.width(18.dp))

                    Text(
                        text = "Tag friends...",
                        color = Color(0xFF777078),
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "+",
                            color = PublishPurple,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "ADD",
                            color = PublishPurple,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))


                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(74.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1D1B1E))
                            .border(
                                1.dp,
                                PublishBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                audienceMenuExpanded = true
                            }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF174D57)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = Color(0xFF91E4FF),
                                modifier = Modifier.size(23.dp)
                            )
                        }

                        Spacer(Modifier.width(15.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Audience",
                                color = PublishText,
                                fontFamily = FontFamily(
                                    Font(R.font.sora_extrabold)
                                ),
                                fontSize = 14.sp
                            )

                            Text(
                                text = selectedAudience,
                                fontFamily = FontFamily(
                                    Font(R.font.sora_regular)
                                ),
                                color = PublishMuted,
                                fontSize = 12.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Change audience",
                            tint = PublishPurple,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = audienceMenuExpanded,
                        onDismissRequest = {
                            audienceMenuExpanded = false
                        },
                        modifier = Modifier
                            .background(Color(0xFF252027))
                    ) {
                        listOf("PUBLIC", "PRIVATE").forEach { audience ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = audience,
                                        color = if (selectedAudience == audience) {
                                            PublishPurple
                                        } else {
                                            PublishText
                                        },
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    selectedAudience = audience
                                    audienceMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Post Performance Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (publishUiState.isLoading) {
                                PublishPurple.copy(alpha = 0.5f)
                            } else {
                                PublishPurple
                            }
                        )
                        .clickable(
                            enabled = !publishUiState.isLoading
                        ) {
                            // The ID returned by POST /performances.
                            val currentPerformanceId = performanceId

                            if (currentPerformanceId == null ||
                                currentPerformanceId <= 0
                            ) {
                                Toast.makeText(
                                    context,
                                    "Invalid performance ID. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@clickable
                            }

                            // Convert the dropdown selection into the
                            // audience value expected by the backend.
                            val apiAudience = selectedAudience

                            // Static collaborator IDs for now.
                            val collaboratorIds = listOf(10, 15)

                            // Submit IDs only for hashtags that the user
                            // selected from the hashtag suggestion chips.
                            val hashtagIds = selectedHashtagIds.toList()

                            // Call the publishing ViewModel.
                            publishPerformanceViewModel.publishPerformance(
                                performanceId = currentPerformanceId,
                                description = description.trim(),
                                audience = apiAudience,
                                collaboratorIds = collaboratorIds,
                                hashtagIds = hashtagIds
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (publishUiState.isLoading) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color(0xFF51116C),
                                strokeWidth = 2.dp
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = "Publishing...",
                                color = Color(0xFF51116C),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Post Performance",
                                color = Color(0xFF51116C),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(Modifier.width(7.dp))

                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Publish performance",
                                tint = Color(0xFF51116C),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
private fun extractActiveHashtag(
    description: String
): String? {

    val hashtagIndex = description.lastIndexOf('#')

    if (hashtagIndex < 0) {
        return null
    }

    val query = description.substring(hashtagIndex + 1)

    // Do not search when # is the last character.
    if (query.isBlank()) {
        return null
    }

    // Stop searching this hashtag when the user starts
    // a new word after a space.
    if (query.any { it.isWhitespace() }) {
        return null
    }

    return query
}

private fun insertSelectedHashtag(
    description: String,
    hashtag: String
): String {

    val hashtagIndex = description.lastIndexOf('#')

    if (hashtagIndex < 0) {
        return description
    }

    val normalizedName = hashtag
        .trim()
        .removePrefix("#")

    // Remove the partial hashtag and insert the selected one.
    val prefix = description.substring(
        0,
        hashtagIndex
    )

    return "$prefix#$normalizedName "
}
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFFD0C3D0),
        fontSize = 11.sp,
        fontFamily = FontFamily(Font(R.font.sora_extrabold)),
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp
    )
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPreviewCard(
    duration: String,
    thumbnailUrl: String?,
    audioUrl: String?,
    recordingPath: String?,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    // Resolve relative upload URLs into complete URLs.
    val fullThumbnailUrl = remember(thumbnailUrl) {
        MediaUrlHelper.getFullUrl(thumbnailUrl)
    }

    val fullAudioUrl = remember(audioUrl) {
        MediaUrlHelper.getFullUrl(audioUrl)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var playbackError by remember {
        mutableStateOf(false)
    }

    val player = remember(context) {
        ExoPlayer.Builder(context).build()
    }

    // Observe playback and release the player when leaving this screen.
    DisposableEffect(player) {
        val listener = object : Player.Listener {

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(
                error: androidx.media3.common.PlaybackException
            ) {
                playbackError = true

                android.util.Log.e(
                    "PublishPerformance",
                    "Audio playback failed: ${error.message}",
                    error
                )
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, fullAudioUrl, recordingPath) {
        player.stop()
        player.clearMediaItems()

        playbackError = false

        when {
            !fullAudioUrl.isNullOrBlank() -> {
                player.setMediaItem(
                    MediaItem.fromUri(fullAudioUrl)
                )
                player.prepare()
            }

            !recordingPath.isNullOrBlank() &&
                    File(recordingPath).exists() -> {

                player.setMediaItem(
                    MediaItem.fromUri(
                        Uri.fromFile(File(recordingPath))
                    )
                )
                player.prepare()
            }

            else -> {
                android.util.Log.e(
                    "PublishPerformance",
                    "No valid audio URL or local recording path"
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF20202F))
            .border(
                width = 1.dp,
                color = PublishPurple,
                shape = RoundedCornerShape(12.dp)
            )
    ) {

        AsyncImage(
            model = fullThumbnailUrl,
            contentDescription = "Recorded performance thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(
                R.drawable.karaoke_singer
            ),
            error = painterResource(
                R.drawable.karaoke_singer
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.20f)
                )
        )

        if (isPlaying) {
            NeonEqualizer(
                isPlaying = isPlaying,
                audioSessionId = player.audioSessionId,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 8.dp
                    )
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(56.dp)
                .clip(CircleShape)
                .background(PublishPurple)
                .clickable {
                    onClick()

                    when {
                        player.isPlaying -> {
                            player.pause()
                        }

                        playbackError -> {
                            // Retry preparing the current media.
                            playbackError = false
                            player.prepare()
                            player.play()
                        }

                        player.playbackState == Player.STATE_ENDED -> {
                            player.seekTo(0)
                            player.play()
                        }

                        else -> {
                            player.play()
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) {
                    androidx.compose.material.icons.Icons.Default.Pause
                } else {
                    androidx.compose.material.icons.Icons.Default.PlayArrow
                },
                contentDescription = if (isPlaying) {
                    "Pause recorded performance"
                } else {
                    "Play recorded performance"
                },
                tint = Color(0xFF51116C),
                modifier = Modifier.size(33.dp)
            )
        }

//        // Duration label.
//        Row(
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(start = 14.dp, bottom = 12.dp)
//                .clip(RoundedCornerShape(6.dp))
//                .background(Color(0xCC080A10))
//                .padding(
//                    horizontal = 7.dp,
//                    vertical = 4.dp
//                ),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Default.Timer,
//                contentDescription = null,
//                tint = Color(0xFF91E4FF),
//                modifier = Modifier.size(14.dp)
//            )

            Spacer(Modifier.width(4.dp))

//            Text(
//                text = duration,
//                color = Color(0xFF91E4FF),
//                fontSize = 12.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
    }
}

@Composable
private fun DescriptionCard(
    description: String,
    onDescriptionChange: (String) -> Unit,
    hashtags: List<com.karoke.myoyasing.domain.model.Hashtag>,
    isLoading: Boolean,
    noResults: Boolean,
    error: String?,
    isSearching: Boolean,
    onHashtagClick: (
        com.karoke.myoyasing.domain.model.Hashtag
    ) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF211F21),
                        Color(0xFF1B1A1C)
                    )
                )
            )
            .border(
                1.dp,
                Color(0xFF403442),
                RoundedCornerShape(13.dp)
            )
            .clickable {
                focusRequester.requestFocus()
            }
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            BasicTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color = PublishText,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (description.isEmpty()) {
                            Text(
                                text = "Add a description...",
                                fontFamily = FontFamily(
                                    Font(R.font.sora_regular)
                                ),
                                color = Color(0xFF817882),
                                fontSize = 12.sp
                            )
                        }

                        innerTextField()
                    }
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        when {
            isSearching && isLoading -> {
                Text(
                    text = "Searching hashtags...",
                    color = PublishMuted,
                    fontSize = 11.sp
                )
            }

            isSearching && error != null -> {
                Text(
                    text = error,
                    color = Color(0xFFFF8A80),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            isSearching && noResults -> {
                Text(
                    text = "No hashtags found",
                    color = Color(0xFFFF8A80),
                    fontSize = 11.sp
                )
            }

            isSearching && hashtags.isNotEmpty() -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    hashtags.take(3).forEach { hashtag ->
                        HashtagChip(
                            text = "#${hashtag.name}",
                            onClick = {
                                onHashtagClick(hashtag)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HashtagChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFF352A38))
            .border(
                1.dp,
                Color(0xFF624568),
                CircleShape
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 9.dp,
                vertical = 6.dp
            )
    ) {
        Text(
            text = text,
            color = PublishPurple,
            fontFamily = FontFamily(
                Font(R.font.sora_extrabold)
            ),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HashtagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFF352A38))
            .border(
                1.dp,
                Color(0xFF624568),
                CircleShape
            )
            .padding(horizontal = 9.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = PublishPurple,
            fontFamily = FontFamily(Font(R.font.sora_extrabold)),
            fontSize = 12.sp,

        )
    }
}

//@Preview(
//    showBackground = true,
//    showSystemUi = true,
//    device = "spec:width=393dp,height=852dp,dpi=440"
//)
//@Composable
//fun PublishPerformanceScreenPreview() {
//    MaterialTheme {
//        PublishPerformanceScreen(
//            navController = NavController(),
//            songTitle = "Levitating",
//            duration = "03:45",
//            onBackClick = {},
//            onSettingsClick = {},
//            onPreviewClick = {},
//            onCollaboratorsClick = {},
//            onAudienceClick = {},
//            onPublishClick = {}
//        )
//    }
//}