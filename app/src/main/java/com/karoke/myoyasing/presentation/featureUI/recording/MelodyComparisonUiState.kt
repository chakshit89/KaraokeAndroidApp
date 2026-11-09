package com.karoke.myoyasing.presentation.featureUI.recording

import com.karoke.myoyasing.domain.model.MelodyComparisonResult

data class MelodyComparisonUiState(

    val isLoading: Boolean = false,

    val result: MelodyComparisonResult? = null,

    val errorMessage: String? = null
)