package com.rotary.hospital.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.feature.home.domain.usecase.GetTermsHtmlUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TermsUiState(
    val isLoading: Boolean = false,
    val html: String? = null,
    val error: String? = null
)

class TermsViewModel(
    private val getTermsHtmlUseCase: GetTermsHtmlUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TermsUiState(isLoading = true))
    val state: StateFlow<TermsUiState> = _state

    init {
        load()
    }

    fun load() {
        _state.value = TermsUiState(isLoading = true)
        viewModelScope.launch {
            runCatching { getTermsHtmlUseCase() }
                .onSuccess { content -> _state.value = TermsUiState(isLoading = false, html = content.html) }
                .onFailure { t -> _state.value = TermsUiState(isLoading = false, error = t.message ?: "Unknown") }
        }
    }
}
