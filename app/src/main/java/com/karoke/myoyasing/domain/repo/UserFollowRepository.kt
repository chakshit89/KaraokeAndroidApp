package com.karoke.myoyasing.domain.repo

interface UserFollowRepository {

    suspend fun followUser(userId: Int): Result<Unit>

    suspend fun unfollowUser(userId: Int): Result<Unit>
}