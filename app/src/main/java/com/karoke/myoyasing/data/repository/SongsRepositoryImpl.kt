package com.karoke.myoyasing.data.repository

import com.karoke.myoyasing.data.dto.SongsDto.SongDto
import com.karoke.myoyasing.data.mapper.toDomain
import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.data.remote.SafeApiCall
import com.karoke.myoyasing.di.network.OkHttpProvider
import com.karoke.myoyasing.domain.model.Song
import com.karoke.myoyasing.domain.repo.SongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import kotlin.coroutines.cancellation.CancellationException

class SongsRepositoryImpl(
    private val apiService: LoginApiService
) : SongsRepository {

    override suspend fun getSongs(): ApiResult<List<Song>> {

        return when (
            val result = SafeApiCall.execute<List<SongDto>> {
                apiService.getSongs()
            }
        ) {
            is ApiResult.Error -> result

            is ApiResult.Success -> {
                ApiResult.Success(
                    result.data.map { it.toDomain() }
                )
            }
        }
    }


    override suspend fun getSongById(
        songId: String
    ): ApiResult<Song> {
        return when (
            val result = SafeApiCall.execute<SongDto> {
                apiService.getSongById(songId)
            }
        ) {
            is ApiResult.Error -> result
            is ApiResult.Success -> {
                ApiResult.Success(result.data.toDomain())
            }
        }
    }

    override suspend fun getLyrics(
        lyricsUrl: String
    ): ApiResult<String> {
        return try {
            val text = withContext(Dispatchers.IO) {
                OkHttpProvider.client.newCall(
                    Request.Builder()
                        .url(lyricsUrl)
                        .get()
                        .build()
                ).execute().use { response ->
                    if (!response.isSuccessful) {
                        error("Unable to load lyrics: HTTP ${response.code}")
                    }

                    response.body?.string()
                        ?.takeIf { it.isNotBlank() }
                        ?: error("The lyrics file is empty.")
                }
            }

            ApiResult.Success(text)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Error(
                message = e.message ?: "Unable to load song lyrics."
            )
        }
    }

}