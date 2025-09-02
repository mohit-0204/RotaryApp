package com.rotary.hospital.feature.patient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.data.model.Patient
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.feature.patient.domain.usecase.GetRegisteredPatientsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PatientListState {
    object Idle : PatientListState()
    object Loading : PatientListState()
    data class Success(val patients: List<Patient>) : PatientListState()
    data class Error(val message: String) : PatientListState()
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
            val result = getRegisteredPatientsUseCase(phoneNumber)
            _state.value = when {
                result.isSuccess -> PatientListState.Success(result.getOrNull()!!)
                else -> PatientListState.Error(
                    result.exceptionOrNull()?.message ?: "Error fetching patients"
                )
            }
            filterPatients()
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
        Logger.d("FilterPatients", "Query: '$query'")
        val state = _state.value
        if (state is PatientListState.Success) {
            val filteredPatients = if (query.isEmpty()) {
                state.patients
            } else {
                state.patients.filter { patient ->
                    Logger.d(
                        "FilterPatients",
                        "Checking patient: ${patient.name}, ID: ${patient.id}"
                    )
                    patient.name.trim().contains(query, ignoreCase = true) ||
                            patient.id.contains(query, ignoreCase = true)
                }.also { it ->
                    Logger.d("FilterPatients", "Filtered patients: ${it.map { it.name }}")
                }
            }
            _state.value = PatientListState.Success(filteredPatients)
        }
    }
}