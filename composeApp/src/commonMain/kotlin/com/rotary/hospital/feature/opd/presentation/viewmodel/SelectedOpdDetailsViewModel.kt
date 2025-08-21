package com.rotary.hospital.feature.opd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.feature.opd.domain.model.OpdDetails
import com.rotary.hospital.feature.opd.domain.usecase.GetOpdDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SelectedOpdDetailsViewModel(
    private val getOpdDetailsUseCase: GetOpdDetailsUseCase

) : ViewModel() {
    private val _state = MutableStateFlow<SelectedOpdDetailsState>(SelectedOpdDetailsState.Idle)
    val state: StateFlow<SelectedOpdDetailsState> = _state.asStateFlow()

    fun fetchOpdDetails(opdId: String) {
        viewModelScope.launch {
            _state.value = SelectedOpdDetailsState.Loading
            getOpdDetailsUseCase(opdId).fold(
                onSuccess = { opd ->
                    _state.value = opd?.let { SelectedOpdDetailsState.Success(it) }
                        ?: SelectedOpdDetailsState.Error("No OPD found")
                },
                onFailure = { error ->
                    _state.value = SelectedOpdDetailsState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed interface SelectedOpdDetailsState {
    data object Idle : SelectedOpdDetailsState
    data object Loading : SelectedOpdDetailsState
    data class Success(val opd: OpdDetails) : SelectedOpdDetailsState
    data class Error(val message: String) : SelectedOpdDetailsState
}