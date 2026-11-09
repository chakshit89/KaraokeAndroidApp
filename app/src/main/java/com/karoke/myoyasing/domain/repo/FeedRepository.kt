package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.domain.model.FeedItem

interface FeedRepository {

    suspend fun getFeed(): Result<List<FeedItem>>
}