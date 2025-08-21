package com.rotary.hospital.feature.opd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.feature.opd.domain.model.Patient
import com.rotary.hospital.feature.opd.domain.usecase.GetRegisteredPatientsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OpdPatientListViewModel(
    private val getRegisteredPatientsUseCase: GetRegisteredPatientsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<OpdPatientListState>(OpdPatientListState.Idle)
    val state: StateFlow<OpdPatientListState> = _state.asStateFlow()

    fun fetchPatients(mobileNumber: String) {
        viewModelScope.launch {
            _state.value = OpdPatientListState.Loading
            getRegisteredPatientsUseCase(mobileNumber).fold(
                onSuccess = { patients -> _state.value = OpdPatientListState.Success(patients) },
                onFailure = { error ->
                    _state.value = OpdPatientListState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed interface OpdPatientListState {
    data object Idle : OpdPatientListState
    data object Loading : OpdPatientListState
    data class Success(val patients: List<Patient>) : OpdPatientListState
    data class Error(val message: String) : OpdPatientListState
}