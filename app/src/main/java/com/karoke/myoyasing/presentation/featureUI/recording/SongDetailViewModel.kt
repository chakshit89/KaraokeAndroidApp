package com.karoke.myoyasing.presentation.featureUI.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.data.remote.ApiResult
import com.karoke.myoyasing.domain.usecase.GetSongByIdUseCase
import com.karoke.myoyasing.domain.usecase.GetSongLyricsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SongDetailViewModel(
    private val getSongByIdUseCase: GetSongByIdUseCase,
    private val getSongLyricsUseCase: GetSongLyricsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadSong(songId: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = SongDetailUiState(isLoading = true)

            try {
                when (val result = getSongByIdUseCase(songId)) {
                    is ApiResult.Success -> {
                        val song = result.data

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                song = song,
                                isLyricsLoading = true,
                                errorMessage = null
                            )
                        }

                        loadLyrics(song.lyricsUrl)
                    }

                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                            ?: "Unable to load this song."
                    )
                }
            }
        }
    }

    private suspend fun loadLyrics(lyricsUrl: String) {
        try {
            when (val result = getSongLyricsUseCase(lyricsUrl)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLyricsLoading = false,
                            lyricsText = result.data,
                            lyricsErrorMessage = null
                        )
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLyricsLoading = false,
                            lyricsErrorMessage = result.message
                        )
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLyricsLoading = false,
                    lyricsErrorMessage = e.message
                        ?: "Unable to load lyrics."
                )
            }
        }
    }
}