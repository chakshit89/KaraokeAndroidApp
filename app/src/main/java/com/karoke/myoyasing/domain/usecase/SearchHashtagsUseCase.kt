package com.karoke.myoyasing.domain.usecase

import com.karoke.myoyasing.domain.model.Hashtag
import com.karoke.myoyasing.domain.repo.HashtagRepository

class SearchHashtagsUseCase(
    private val repository: HashtagRepository
) {

    suspend operator fun invoke(
        query: String
    ): Result<List<Hashtag>> {

        val normalizedQuery = query.trim()

        if (normalizedQuery.isBlank()) {
            return Result.success(emptyList())
        }

        return repository.searchHashtags(normalizedQuery)
    }
}