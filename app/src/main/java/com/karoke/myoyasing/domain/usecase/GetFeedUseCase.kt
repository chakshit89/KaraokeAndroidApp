package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.model.FeedItem
import com.karoke.myoyasing.domain.repo.FeedRepository

class GetFeedUseCase(
    private val repository: FeedRepository
) {

    suspend operator fun invoke(): Result<List<FeedItem>> {
        return repository.getFeed()
    }
}