package com.karoke.myoyasing.domain.repo

import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.model.Song

interface SongsRepository {

    suspend fun getSongs(): ApiResult<List<Song>>

    suspend fun getSongById(songId: String): ApiResult<Song>

    suspend fun getLyrics(lyricsUrl: String): ApiResult<String>
}