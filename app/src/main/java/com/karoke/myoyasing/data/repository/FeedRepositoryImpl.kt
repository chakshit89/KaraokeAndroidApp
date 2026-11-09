package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.mapper.toDomain
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.model.FeedItem
import com.karoke.myoyasing.domain.repo.FeedRepository
import kotlin.coroutines.cancellation.CancellationException

class FeedRepositoryImpl(
    private val apiService: LoginApiService
) : FeedRepository {

    override suspend fun getFeed(): Result<List<FeedItem>> {
        return try {
            val response = apiService.getFeed()

            if (!response.isSuccessful) {
                val errorMessage = response.errorBody()
                    ?.string()
                    ?.takeIf { it.isNotBlank() }
                    ?: "Failed to load feed: HTTP ${response.code()}"

                return Result.failure(
                    Exception(errorMessage)
                )
            }

            val body = response.body()
                ?: return Result.failure(
                    Exception("Feed response is empty")
                )

            if (!body.success) {
                return Result.failure(
                    Exception(
                        body.message.ifBlank {
                            "Unable to retrieve feed"
                        }
                    )
                )
            }

            val feedItems = body.data.map { it.toDomain() }

            Result.success(feedItems)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "Something went wrong while loading feed",
                    e
                )
            )
        }
    }
}