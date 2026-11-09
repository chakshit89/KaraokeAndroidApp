package com.karoke.myoyasing.presentation.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.domain.usecase.AddPerformanceCommentUseCase
import com.karoke.myoyasing.domain.usecase.DeletePerformanceCommentUseCase
import com.karoke.myoyasing.domain.usecase.GetPerformanceCommentsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class PerformanceCommentsViewModel(
    private val getPerformanceCommentsUseCase: GetPerformanceCommentsUseCase,
    private val addPerformanceCommentUseCase: AddPerformanceCommentUseCase,
    private val deletePerformanceCommentUseCase: DeletePerformanceCommentUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(PerformanceCommentsUiState())

    val uiState: StateFlow<PerformanceCommentsUiState> =
        _uiState.asStateFlow()

    private var currentPerformanceId: Long? = null

    private val pageSize = 20

    private val sortOrder = listOf("createdAt,desc")
    private var commentsJob: Job? = null

    fun loadComments(
        performanceId: Long,
        refresh: Boolean = false
    ) {

        if (performanceId <= 0) return

        val state = _uiState.value

        val isSamePerformance = currentPerformanceId == performanceId
        currentPerformanceId = performanceId

        _uiState.value = state.copy(
            isLoading = true,
            error = null,
            comments = if (isSamePerformance) {
                state.comments
            } else {
                emptyList()
            },
            currentPage = 0,
            hasMorePages = true
        )

        if (
            currentPerformanceId == performanceId &&
            (state.isLoading || state.isLoadingMore)
        ) return

        if (!refresh &&
            currentPerformanceId == performanceId &&
            state.comments.isNotEmpty()
        ) return

        currentPerformanceId = performanceId

        _uiState.value = state.copy(
            isLoading = true,
            error = null,
            comments = if (refresh) emptyList() else state.comments,
            currentPage = 0,
            hasMorePages = true
        )

        fetchComments(
            performanceId = performanceId,
            page = 0,
            isFirstPage = true
        )
    }

    fun loadMoreComments() {

        val state = _uiState.value
        val performanceId = currentPerformanceId ?: return

        if (state.isLoading ||
            state.isLoadingMore ||
            !state.hasMorePages
        ) return

        fetchComments(
            performanceId = performanceId,
            page = state.currentPage + 1,
            isFirstPage = false
        )
    }

    private fun fetchComments(
        performanceId: Long,
        page: Int,
        isFirstPage: Boolean
    ) {
        // Set loading state immediately to prevent duplicate requests.
        _uiState.value = _uiState.value.copy(
            isLoading = isFirstPage,
            isLoadingMore = !isFirstPage,
            error = null
        )

        commentsJob?.cancel()

        commentsJob = viewModelScope.launch {
            try {
                getPerformanceCommentsUseCase(
                    performanceId = performanceId,
                    page = page,
                    size = pageSize,
                    sort = sortOrder
                ).onSuccess { newComments ->

                    if (currentPerformanceId != performanceId) {
                        return@onSuccess
                    }

                    val currentComments = _uiState.value.comments

                    val updatedComments = if (isFirstPage) {
                        newComments
                    } else {
                        (currentComments + newComments)
                            .distinctBy { it.commentId }
                    }

                    _uiState.value = _uiState.value.copy(
                        comments = updatedComments,
                        isLoading = false,
                        isLoadingMore = false,
                        currentPage = page,
                        hasMorePages = newComments.size >= pageSize,
                        error = null
                    )

                }.onFailure { exception ->

                    if (currentPerformanceId != performanceId) {
                        return@onFailure
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = exception.message
                            ?: "Unable to load comments"
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (currentPerformanceId == performanceId) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = e.message ?: "Unable to load comments"
                    )
                }
            }
        }
    }



    fun addComment(
        performanceId: Long,
        content: String
    ) {
        val message = content.trim()

        if (message.isBlank()) return
        if (_uiState.value.isAddingComment) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAddingComment = true,
                addCommentError = null,
                commentAdded = false
            )

            try {
                addPerformanceCommentUseCase(
                    performanceId = performanceId,
                    content = message
                ).onSuccess { newComment ->

                    // Only update the currently opened performance.
                    if (currentPerformanceId == performanceId) {

                        // Immediately insert the POST response into the list.
                        val currentState = _uiState.value

                        val updatedComments =
                            (listOf(newComment) + currentState.comments)
                                .distinctBy { it.commentId }

                        _uiState.value = currentState.copy(
                            comments = updatedComments,
                            isAddingComment = false,
                            isLoading = false,
                            isLoadingMore = false,
                            commentAdded = true,
                            error = null
                        )

                    } else {
                        _uiState.value = _uiState.value.copy(
                            isAddingComment = false
                        )
                    }
                }.onFailure { exception ->

                    _uiState.value = _uiState.value.copy(
                        isAddingComment = false,
                        addCommentError = exception.message
                            ?: "Failed to add comment"
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddingComment = false,
                    addCommentError = e.message
                        ?: "Failed to add comment"
                )
            }
        }
    }



    fun deleteComment(
        performanceId: Long,
        commentId: Long
    ) {
        if (commentId in _uiState.value.deletingCommentIds) return

        _uiState.value = _uiState.value.copy(
            deletingCommentIds =
                _uiState.value.deletingCommentIds + commentId,
            deleteCommentError = null,
            commentDeleted = false
        )

        viewModelScope.launch {

            try {
                deletePerformanceCommentUseCase(
                    performanceId = performanceId,
                    commentId = commentId
                ).onSuccess {

                    val currentState = _uiState.value

                    _uiState.value = currentState.copy(
                        comments = currentState.comments.filterNot {
                            it.commentId == commentId
                        },
                        deletingCommentIds =
                            currentState.deletingCommentIds - commentId,
                        commentDeleted = true
                    )

                    // Fetch the latest comments from the backend.
                    if (currentPerformanceId == performanceId) {
                        loadComments(
                            performanceId = performanceId,
                            refresh = true
                        )
                    }

                }.onFailure { exception ->

                    _uiState.value = _uiState.value.copy(
                        deletingCommentIds =
                            _uiState.value.deletingCommentIds - commentId,
                        deleteCommentError = exception.message
                            ?: "Failed to delete comment"
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    deletingCommentIds =
                        _uiState.value.deletingCommentIds - commentId,
                    deleteCommentError = e.message
                        ?: "Failed to delete comment"
                )
            }
        }
    }


    fun clearEvents() {

        _uiState.value = _uiState.value.copy(
            commentAdded = false,
            commentDeleted = false,
            addCommentError = null,
            deleteCommentError = null
        )
    }
}