package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.domain.model.Hashtag

interface HashtagRepository {

    suspend fun searchHashtags(
        query: String
    ): Result<List<Hashtag>>
}