package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.repo.UserFollowRepository

class FollowUserUseCase(
    private val repository: UserFollowRepository
) {
    suspend operator fun invoke(userId: Int): Result<Unit> {
        return repository.followUser(userId)
    }
}