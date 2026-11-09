package com.karoke.myoyasing.presentation.featureUI.recording

import com.karoke.myoyasing.domain.model.Song

data class SongDetailUiState(
    val isLoading: Boolean = false,
    val song: Song? = null,
    val lyricsText: String? = null,
    val isLyricsLoading: Boolean = false,
    val errorMessage: String? = null,
    val lyricsErrorMessage: String? = null
)