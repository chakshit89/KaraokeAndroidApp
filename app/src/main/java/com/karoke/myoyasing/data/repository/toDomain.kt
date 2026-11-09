package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.dto.CommentsDTO.PerformanceCommentDto
import com.karoke.myoyasing.domain.model.PerformanceComment

fun PerformanceCommentDto.toDomain(): PerformanceComment {
    return PerformanceComment(
        commentId = commentId,
        performanceId = performanceId,
        userId = userId,
        username = username,
        content = content,
        createdAt = createdAt
    )
}