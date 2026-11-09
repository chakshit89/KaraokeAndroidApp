package com.karoke.myoyasing.presentation.feed.viewmodel

import com.karoke.myoyasing.domain.model.PerformanceComment

data class PerformanceCommentsUiState(

    val comments: List<PerformanceComment> = emptyList(),

    // GET
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,

    // POST
    val isAddingComment: Boolean = false,

    // DELETE
    val deletingCommentIds: Set<Long> = emptySet(),

    // Pagination
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,

    // Errors
    val error: String? = null,
    val addCommentError: String? = null,
    val deleteCommentError: String? = null,

    // Success feedback
    val commentAdded: Boolean = false,
    val commentDeleted: Boolean = false
)