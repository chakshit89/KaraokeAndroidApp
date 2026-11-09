package com.karoke.myoyasing.presentation.featureUI.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.domain.model.UploadedMedia
import com.karoke.myoyasing.domain.usecase.UploadMediaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class MediaUploadUiState(
    val isUploading: Boolean = false,
    val uploadedMedia: UploadedMedia? = null,
    val errorMessage: String? = null
)

class MediaUploadViewModel(
    private val uploadMediaUseCase: UploadMediaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaUploadUiState())
    val uiState: StateFlow<MediaUploadUiState> =
        _uiState.asStateFlow()

    fun uploadAudio(file: File,thumbnailUrl: String) {
        if (_uiState.value.isUploading) return

        _uiState.value = MediaUploadUiState(isUploading = true)

        viewModelScope.launch {
            uploadMediaUseCase(file,thumbnailUrl)
                .onSuccess { media ->
                    _uiState.value = MediaUploadUiState(
                        isUploading = false,
                        uploadedMedia = media
                    )
                }
                .onFailure { error ->
                    _uiState.value = MediaUploadUiState(
                        isUploading = false,
                        errorMessage = error.message
                            ?: "Unable to upload recording."
                    )
                }
        }
    }

    fun clearResult() {
        _uiState.value = MediaUploadUiState()
    }
}