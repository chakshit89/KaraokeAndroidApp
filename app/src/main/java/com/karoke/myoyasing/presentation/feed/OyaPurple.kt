package com.karoke.myoyasing.presentation.feed

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.karoke.myoyasing.domain.model.PerformanceComment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val OyaPurple = Color(0xFFE6A6FF)
private val OyaBackground = Color(0xFF17121D)
private val OyaCard = Color(0xFF251D2D)
private val OyaWhite = Color(0xFFF8F4FA)
private val OyaMuted = Color(0xFFB9AFBF)
private val OyaPink = Color(0xFFFF72C6)


data class PerformanceCommentUi(
    val commentId: Long,
    val userId: Long,
    val username: String,
    val profileImageUrl: String? = null,
    val content: String,
    val createdAt: String,
    val likes: Int = 0,
    val isLiked: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceCommentsBottomSheet(
    visible: Boolean,
    performanceId: Long,
    comments: List<PerformanceCommentUi>,
    totalComments: Int,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasMoreComments: Boolean,
    isSendingComment: Boolean,

    currentUserId: String ,
    commentAdded: Boolean = false,
    commentDeleted: Boolean = false,

    onCommentAddedHandled: () -> Unit = {},
    onCommentDeletedHandled: () -> Unit = {},

    error: String? = null,
    onRetry: () -> Unit = {},
    onDismiss: () -> Unit,
    onSendComment: (String) -> Unit,
    onLoadMore: () -> Unit,
    onDeleteComment: (Long) -> Unit,
    deletingCommentIds: Set<Long> = emptySet(),

    modifier: Modifier = Modifier
) {

    if (!visible) return
    val currentUserIdLong = currentUserId?.toLongOrNull()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var commentText by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(commentAdded) {
        if (commentAdded) {

            commentText = ""

            keyboardController?.hide()
            focusManager.clearFocus()
            listState.animateScrollToItem(0)

            Toast.makeText(
                context,
                "Comment added",
                Toast.LENGTH_SHORT
            ).show()

            onCommentAddedHandled()
        }
    }

    LaunchedEffect(commentDeleted) {
        if (commentDeleted) {
            onCommentDeletedHandled()
        }
    }
    var lastRequestedSize by remember {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(performanceId) {
        lastRequestedSize = -1
        commentText = ""
        listState.scrollToItem(0)
    }

    LaunchedEffect(listState, comments.size, hasMoreComments, isLoadingMore) {

        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItems = layoutInfo.totalItemsCount

            totalItems > 0 && lastVisibleItem >= totalItems - 3
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                if (
                    hasMoreComments &&
                    !isLoadingMore &&
                    !isLoading &&
                    lastRequestedSize != comments.size
                ) {
                    lastRequestedSize = comments.size
                    onLoadMore()
                }
            }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = OyaBackground,
        contentColor = OyaWhite,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(42.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(OyaMuted.copy(alpha = 0.5f))
            )
        }
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 720.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {

            CommentsHeader(
                totalComments = totalComments
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f)
            )

            // COMMENTS LIST
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 540.dp),
                    contentPadding = PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        top = 18.dp,
                        bottom = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {

                    items(
                        items = comments,
                        key = { it.commentId }
                    ) { comment ->
                        CommentItem(
                            comment = comment,
                            isDeleting = comment.commentId in deletingCommentIds,
                            canDelete = currentUserIdLong != null &&
                                    comment.userId == currentUserIdLong,
                            onDeleteClick = {
                                onDeleteComment(comment.commentId)
                            }
                        )
                    }


                    if (isLoading && comments.isEmpty()) {
                        item {
                            LoadingComments()
                        }
                    }
                    if (error != null && comments.isEmpty() && !isLoading) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(30.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = error,
                                    color = OyaWhite
                                )

                                TextButton(
                                    onClick = onRetry
                                ) {
                                    Text(
                                        text = "Retry",
                                        color = OyaPurple
                                    )
                                }
                            }
                        }
                    }
                    // Empty state
                    if (!isLoading && error == null && comments.isEmpty()) {
                        item {
                            EmptyComments()
                        }
                    }

                    // Pagination loading
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = OyaPurple,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f)
            )


            CommentInputField(
                text = commentText,
                onTextChange = { commentText = it },
                isSending = isSendingComment,
                onSend = {
                    val message = commentText.trim()

                    if (message.isNotEmpty() && !isSendingComment) {
                        onSendComment(message)
                        commentText=""

                    }
                }
            )
        }
    }
}


@Composable
private fun CommentsHeader(
    totalComments: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Comments",
            color = OyaWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(OyaPurple.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = formatCommentCount(totalComments),
                color = OyaPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatCommentCount(count: Int): String {
    return when {
        count >= 1_000_000 ->
            String.format("%.1fM", count / 1_000_000.0)

        count >= 1_000 ->
            String.format("%.1fK", count / 1_000.0)

        else -> count.toString()
    }
}


@Composable
private fun CommentItem(
    comment: PerformanceCommentUi,
    isDeleting: Boolean,
    canDelete: Boolean,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = comment.profileImageUrl,
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(OyaCard)
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.username,
                    color = OyaWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = formatRelativeTime(comment.createdAt),
                    color = OyaMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = comment.content,
                color = OyaWhite.copy(alpha = 0.92f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }

        Spacer(Modifier.width(8.dp))
        if (canDelete) {
            IconButton(
                onClick = onDeleteClick,
                enabled = !isDeleting,
                modifier = Modifier.size(36.dp)
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete comment",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

    }
}

private fun formatRelativeTime(createdAt: String): String {
    val createdInstant = runCatching {
        Instant.parse(createdAt)
    }.getOrNull() ?: return createdAt

    val seconds = Duration.between(
        createdInstant,
        Instant.now()
    ).seconds.coerceAtLeast(0)

    return when {
        seconds < 10 -> "just now"
        seconds < 60 -> "${seconds}s"

        seconds < 3_600 -> {
            "${seconds / 60}m"
        }

        seconds < 86_400 -> {
            "${seconds / 3_600}h"
        }

        seconds < 604_800 -> {
            "${seconds / 86_400}d"
        }

        else -> {
            DateTimeFormatter
                .ofPattern("d MMM", Locale.getDefault())
                .withZone(ZoneId.systemDefault())
                .format(createdInstant)
        }
    }
}

@Composable
private fun CommentInputField(
    text: String,
    onTextChange: (String) -> Unit,
    isSending: Boolean,
    onSend: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OyaBackground)
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(OyaPurple.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = OyaPurple
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // INPUT
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(25.dp))
                .background(OyaCard)
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.weight(1f)
            ) {

                if (text.isEmpty()) {
                    Text(
                        text = "Add a comment...",
                        color = OyaMuted,
                        fontSize = 13.sp
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = OyaWhite,
                        fontSize = 13.sp
                    ),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { onSend() }
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))


            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send comment",
                tint = if (text.isNotBlank() && !isSending)
                    OyaPurple
                else
                    OyaMuted.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(21.dp)
                    .clickable(
                        enabled = text.isNotBlank() && !isSending,
                        onClick = onSend
                    )
            )
        }
    }
}

// ------------------------------------------------------
// EMPTY STATE
// ------------------------------------------------------

@Composable
private fun EmptyComments() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 55.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "💬",
            fontSize = 38.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No comments yet",
            color = OyaWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Be the first to appreciate this performance!",
            color = OyaMuted,
            fontSize = 12.sp
        )
    }
}


@Composable
private fun LoadingComments() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 2.dp
        )
    }
}

fun PerformanceComment.toUi(): PerformanceCommentUi {
    return PerformanceCommentUi(
        commentId = commentId,
        userId = userId,
        username = username,
        profileImageUrl = null,
        content = content,
        createdAt = createdAt
    )
}

//@Preview(
//    name = "OyaSing Comments Bottom Sheet",
//    showBackground = true,
//    showSystemUi = true,
//    device = "spec:width=393dp,height=873dp,dpi=440"
//)
//@Composable
//fun PerformanceCommentsBottomSheetPreview() {
//
//    val previewComments = listOf(
//
//        PerformanceCommentUi(
//            commentId = 1L,
//            userId = 101L,
//            username = "Sarah Williams",
//            profileImageUrl = null,
//            content = "Amazing performance! Loved your pitch control. ❤️🎤",
//            createdAt = "2 min ago",
//            likes = 124,
//            isLiked = true
//        ),
//
//        PerformanceCommentUi(
//            commentId = 2L,
//            userId = 102L,
//            username = "Alex Johnson",
//            profileImageUrl = null,
//            content = "Your voice is absolutely beautiful! Keep singing.",
//            createdAt = "10 min ago",
//            likes = 89,
//            isLiked = false
//        ),
//
//        PerformanceCommentUi(
//            commentId = 3L,
//            userId = 103L,
//            username = "Emily Rose",
//            profileImageUrl = null,
//            content = "This is one of my favourite songs and you nailed it! 🔥",
//            createdAt = "20 min ago",
//            likes = 56,
//            isLiked = true
//        ),
//
//        PerformanceCommentUi(
//            commentId = 4L,
//            userId = 104L,
//            username = "David Smith",
//            profileImageUrl = null,
//            content = "Incredible voice! Waiting for your next performance.",
//            createdAt = "35 min ago",
//            likes = 32,
//            isLiked = false
//        ),
//
//        PerformanceCommentUi(
//            commentId = 5L,
//            userId = 105L,
//            username = "Jessica",
//            profileImageUrl = null,
//            content = "Wowww! Such a lovely performance. ❤️",
//            createdAt = "1 hour ago",
//            likes = 18,
//            isLiked = false
//        )
//    )
//
//    MaterialTheme {
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFF100D15)),
//            contentAlignment = Alignment.BottomCenter
//        ) {
//
//            PerformanceCommentsBottomSheet(
//                visible = true,
//                performanceId = 1001L,
//                comments = previewComments,
//                totalComments = 1248,
//                isLoading = false,
//                isLoadingMore = false,
//                hasMoreComments = true,
//                isSendingComment = false,
//                onDismiss = {},
//                onSendComment = {},
//                onLoadMore = {},
//                onDeleteComment = {}
//            )
//        }
//    }
//}