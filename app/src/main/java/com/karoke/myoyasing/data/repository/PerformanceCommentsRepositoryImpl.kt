package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.dto.CommentsDTO.AddCommentRequestDto
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.model.PerformanceComment
import com.karoke.myoyasing.domain.repo.PerformanceCommentsRepository
import kotlin.coroutines.cancellation.CancellationException

class PerformanceCommentsRepositoryImpl(
    private val api: LoginApiService
) : PerformanceCommentsRepository {

    // GET COMMENTS
    override suspend fun getPerformanceComments(
        performanceId: Long,
        page: Int,
        size: Int,
        sort: List<String>?
    ): Result<List<PerformanceComment>> {

        return try {
            val response = api.getPerformanceComments(
                performanceId = performanceId,
                page = page,
                size = size,
                sort = sort
            )

            if (response.isSuccessful) {
                Result.success(
                    response.body().orEmpty().map { it.toDomain() }
                )
            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string()
                            ?: "Failed to fetch comments"
                    )
                )
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPerformanceComment(
        performanceId: Long,
        content: String
    ): Result<PerformanceComment> {

        return try {
            val response = api.addPerformanceComment(
                performanceId = performanceId,
                request = AddCommentRequestDto(content = content)
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(
                        Exception("Empty comment response")
                    )
                }

            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string()
                            ?: "Failed to add comment"
                    )
                )
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePerformanceComment(
        performanceId: Long,
        commentId: Long
    ): Result<Unit> {

        return try {
            val response = api.deletePerformanceComment(
                performanceId = performanceId,
                commentId = commentId
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string()
                            ?: "Failed to delete comment"
                    )
                )
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}