package com.rotary.hospital.feature.patient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.data.model.Patient
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.domain.AppError
import com.rotary.hospital.core.domain.AuthError
import com.rotary.hospital.core.domain.NetworkError
import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.core.domain.ServerError
import com.rotary.hospital.core.domain.UiText
import com.rotary.hospital.feature.patient.domain.usecase.GetRegisteredPatientsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.error_no_internet
import rotaryhospital.composeapp.generated.resources.error_server
import rotaryhospital.composeapp.generated.resources.error_timeout
import rotaryhospital.composeapp.generated.resources.error_unknown

sealed class PatientListState {
    object Idle : PatientListState()
    object Loading : PatientListState()
    data class Success(val patients: List<Patient>, val allPatients: List<Patient>) : PatientListState()
    data class Error(val message: UiText) : PatientListState()
}

class PatientListViewModel(
    private val getRegisteredPatientsUseCase: GetRegisteredPatientsUseCase,
    private val preferences: PreferencesManager
) : ViewModel() {
    private val _state = MutableStateFlow<PatientListState>(PatientListState.Idle)
    val state: StateFlow<PatientListState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterPatients()
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
            filterPatients()
        }
    }

    fun fetchPatients(phoneNumber: String) {
        viewModelScope.launch {
            _state.value = PatientListState.Loading
            when (val result = getRegisteredPatientsUseCase(phoneNumber)) {
                is Result.Success -> {
                    _state.value = PatientListState.Success(result.data, result.data)
                }
                is Result.Error -> {
                    _state.value = PatientListState.Error(mapErrorToUiText(result.error))
                }
            }
        }
    }

    fun saveSelectedPatient(patient: Patient, onSaved: () -> Unit) {
        viewModelScope.launch {
            preferences.saveBoolean(PreferenceKeys.IS_LOGGED_IN, true)
            preferences.saveString(PreferenceKeys.PATIENT_ID, patient.id)
            preferences.saveString(PreferenceKeys.PATIENT_NAME, patient.name)
            preferences.saveString(PreferenceKeys.MOBILE_NUMBER, patient.phoneNumber)
            onSaved()
        }
    }

    private fun filterPatients() {
        val query = _searchQuery.value.trim()
        val currentState = _state.value
        if (currentState is PatientListState.Success) {
            val filteredPatients = if (query.isEmpty()) {
                currentState.allPatients
            } else {
                currentState.allPatients.filter { patient ->
                    patient.name.trim().contains(query, ignoreCase = true) ||
                            patient.id.contains(query, ignoreCase = true)
                }
            }
            _state.value = currentState.copy(patients = filteredPatients)
        }
    }

    private fun mapErrorToUiText(error: AppError): UiText {
        return when (error) {
            is AuthError.ServerMessage -> UiText.DynamicString(error.message)
            is NetworkError.NoInternet -> UiText.StringResource(Res.string.error_no_internet)
            is NetworkError.Timeout -> UiText.StringResource(Res.string.error_timeout)
            is ServerError -> UiText.StringResource(Res.string.error_server)
            else -> UiText.StringResource(Res.string.error_unknown)
        }
    }
}