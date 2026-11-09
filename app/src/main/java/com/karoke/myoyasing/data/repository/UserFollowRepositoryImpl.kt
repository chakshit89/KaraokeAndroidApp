package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.repo.UserFollowRepository
import kotlin.coroutines.cancellation.CancellationException

class UserFollowRepositoryImpl(
    private val api: LoginApiService
) : UserFollowRepository {

    override suspend fun followUser(
        userId: Int
    ): Result<Unit> {
        return try {
            val response = api.followUser(userId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Follow failed: ${response.code()} ${response.message()}")
                )
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unfollowUser(
        userId: Int
    ): Result<Unit> {
        return try {
            val response = api.unfollowUser(userId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Unfollow failed: ${response.code()} ${response.message()}")
                )
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}