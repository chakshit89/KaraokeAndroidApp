package com.karoke.myoyasing.presentation.featureUI.home

import com.karoke.myoyasing.domain.model.Song

data class SongsUiState(
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val errorMessage: String? = null
)