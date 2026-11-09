package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.domain.model.Hashtag
import com.karoke.myoyasing.domain.repo.HashtagRepository

class HashtagRepositoryImpl(
    private val apiService: LoginApiService
) : HashtagRepository {

    override suspend fun searchHashtags(
        query: String
    ): Result<List<Hashtag>> {

        return try {
            val response = apiService.searchHashtags(query)

            Result.success(
                response.map { dto ->
                    Hashtag(
                        id = dto.id,
                        name = dto.name
                    )
                }
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}