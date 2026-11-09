package com.karoke.myoyasing.presentation.publishing.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karoke.myoyasing.domain.model.Hashtag
import com.karoke.myoyasing.domain.usecase.SearchHashtagsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HashtagUiState(
    val query: String = "",
    val hashtags: List<Hashtag> = emptyList(),
    val isLoading: Boolean = false,
    val noResults: Boolean = false,
    val error: String? = null
)

class HashtagViewModel(
    private val searchHashtagsUseCase: SearchHashtagsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HashtagUiState())

    val uiState: StateFlow<HashtagUiState> =
        _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onHashtagQueryChanged(query: String) {
        val normalizedQuery = query.trim()

        searchJob?.cancel()

        if (normalizedQuery.isBlank()) {
            _uiState.value = HashtagUiState()
            return
        }

        _uiState.value = HashtagUiState(
            query = normalizedQuery,
            isLoading = true
        )

        searchJob = viewModelScope.launch {
            delay(350)

            // Ignore a query that has already been replaced.
            if (_uiState.value.query != normalizedQuery) {
                return@launch
            }

            searchHashtagsUseCase(normalizedQuery)
                .onSuccess { results ->

                    if (_uiState.value.query != normalizedQuery) {
                        return@onSuccess
                    }

                    val matches = results
                        .distinctBy { it.id }
                        .take(3)

                    val letterCount =
                        normalizedQuery.count { it.isLetter() }

                    _uiState.value = HashtagUiState(
                        query = normalizedQuery,
                        hashtags = matches,
                        isLoading = false,
                        noResults = matches.isEmpty() &&
                                letterCount >= 5
                    )
                }
                .onFailure { exception ->

                    if (_uiState.value.query != normalizedQuery) {
                        return@onFailure
                    }

                    _uiState.value = HashtagUiState(
                        query = normalizedQuery,
                        isLoading = false,
                        error = exception.message
                            ?: "Unable to search hashtags"
                    )
                }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        searchJob = null
        _uiState.value = HashtagUiState()
    }
}