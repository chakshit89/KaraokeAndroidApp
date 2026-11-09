package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.repo.UserFollowRepository

class UnfollowUserUseCase(
    private val repository: UserFollowRepository
) {
    suspend operator fun invoke(userId: Int): Result<Unit> {
        return repository.unfollowUser(userId)
    }
}