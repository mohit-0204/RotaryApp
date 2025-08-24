package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.ui.screen.SharedListScreen
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPatientListState
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPatientListViewModel
import com.rotary.hospital.feature.patient.presentation.screen.PatientListItem
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OpdPatientListScreen(
    onPatientClick: (String, String) -> Unit,
    onAddPatient: () -> Unit,
    onBack: () -> Unit,
    viewModel: OpdPatientListViewModel = koinViewModel(),
    preferences: PreferencesManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val patients = (state as? OpdPatientListState.Success)?.patients.orEmpty()
    val isLoading = state is OpdPatientListState.Loading
    val error = (state as? OpdPatientListState.Error)?.message

    LaunchedEffect(Unit) {
        preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").collect { pref ->
            if (pref.isNotBlank()) viewModel.fetchPatients(pref)
        }
    }

    SharedListScreen(
        title = "Select Patient for OPD",
        items = patients,
        isLoading = isLoading,
        errorMessage = error,
        emptyMessage = "No patients registered",
        onSearchQueryChange = null,   // no search here
        isSearchActive = false,
        onToggleSearch = null,
        onBack = onBack,
        onAdd = onAddPatient,       // FAB here(pass null if don't want FAB)
        itemContent = { opdPatient, onClick ->
            PatientListItem(
                patient = com.rotary.hospital.core.data.model.Patient(
                    opdPatient.patientId, opdPatient.patientName, ""
                ), onClick = onClick
            )
        },
        onItemClick = { opdPatient ->
            onPatientClick(opdPatient.patientId, opdPatient.patientName)
        })
}