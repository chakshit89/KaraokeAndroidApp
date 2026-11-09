package com.karoke.myoyasing.presentation.featureUI.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.domain.usecase.CompareMelodyUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class MelodyComparisonViewModel(
    private val compareMelodyUseCase: CompareMelodyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MelodyComparisonUiState()
    )

    val uiState: StateFlow<MelodyComparisonUiState> =
        _uiState.asStateFlow()

    fun compareMelody(
        songId: Long,
        userMelodyPath: String
    ) {

        if (_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {

            _uiState.value = MelodyComparisonUiState(
                isLoading = true
            )

            try {

                // File validation and API invocation run off the main thread.
                val result = withContext(Dispatchers.IO) {

                    val melodyFile = File(userMelodyPath)

                    compareMelodyUseCase(
                        songId = songId,
                        userMelodyFile = melodyFile
                    )
                }

                _uiState.value = result.fold(

                    onSuccess = { comparisonResult ->

                        MelodyComparisonUiState(
                            isLoading = false,
                            result = comparisonResult
                        )
                    },

                    onFailure = { exception ->

                        MelodyComparisonUiState(
                            isLoading = false,
                            errorMessage = exception.message
                                ?: "Unable to compare melodies."
                        )
                    }
                )

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.value = MelodyComparisonUiState(
                    isLoading = false,
                    errorMessage = e.message
                        ?: "Something went wrong."
                )
            }
        }
    }

    fun clearResult() {
        _uiState.value = MelodyComparisonUiState()
    }
}