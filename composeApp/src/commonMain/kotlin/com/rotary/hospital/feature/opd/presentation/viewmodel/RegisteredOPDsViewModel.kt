package com.rotary.hospital.feature.opd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.feature.opd.domain.model.Opd
import com.rotary.hospital.feature.opd.domain.usecase.GetBookedOpdsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisteredOPDsViewModel(
    private val getBookedOpdsUseCase: GetBookedOpdsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<RegisteredOPDsState>(RegisteredOPDsState.Idle)
    val state: StateFlow<RegisteredOPDsState> = _state.asStateFlow()

    fun fetchOpdList(mobileNumber: String) {
        viewModelScope.launch {
            _state.value = RegisteredOPDsState.Loading
            getBookedOpdsUseCase(mobileNumber).fold(
                onSuccess = { opdList -> _state.value = RegisteredOPDsState.Success(opdList) },
                onFailure = { error ->
                    _state.value = RegisteredOPDsState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed interface RegisteredOPDsState {
    data object Idle : RegisteredOPDsState
    data object Loading : RegisteredOPDsState
    data class Success(val opdList: List<Opd>) : RegisteredOPDsState
    data class Error(val message: String) : RegisteredOPDsState
}