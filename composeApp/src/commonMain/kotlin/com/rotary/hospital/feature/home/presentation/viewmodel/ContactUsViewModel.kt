package com.rotary.hospital.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.feature.home.domain.usecase.GetContactSectionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactUsViewModel(
    private val getContactSections: GetContactSectionsUseCase
) : ViewModel(){
    private val _ui = MutableStateFlow(ContactUsUiState())
    val ui: StateFlow<ContactUsUiState> = _ui

    fun load() {
        if (!_ui.value.loading) return
        viewModelScope.launch {
             getContactSections()
                .onSuccess { sections ->
                    _ui.update { it.copy(loading = false, sections = sections, error = null) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(loading = false, error = e.message ?: "Unknown error") }
                }
        }
    }
}