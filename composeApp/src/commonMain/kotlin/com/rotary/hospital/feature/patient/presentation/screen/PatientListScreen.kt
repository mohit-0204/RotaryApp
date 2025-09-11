package com.rotary.hospital.feature.patient.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.data.model.Patient
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.ui.screen.SharedListScreen
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientListState
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientListViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.no_match_for
import rotaryhospital.composeapp.generated.resources.no_patients_found
import rotaryhospital.composeapp.generated.resources.p_id
import rotaryhospital.composeapp.generated.resources.registered_patients
import rotaryhospital.composeapp.generated.resources.select_patient

@Composable
fun PatientListScreen(
    phoneNumber: String,
    onAddPatient: () -> Unit,
    onBackClick: () -> Unit,
    onPatientSelected: (String) -> Unit,
    viewModel: PatientListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val patients = (state as? PatientListState.Success)?.patients.orEmpty()
    val isLoading = state is PatientListState.Loading
    val errorUiText = (state as? PatientListState.Error)?.message
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()

    LaunchedEffect(phoneNumber) { viewModel.fetchPatients(phoneNumber) }

    SharedListScreen(
        title               = stringResource(Res.string.registered_patients),
        items               = patients,
        isLoading           = isLoading,
        errorMessage        = errorUiText?.asString(),
        emptyMessage        = if (searchQuery.isEmpty()) stringResource(Res.string.no_patients_found)
                                else stringResource(Res.string.no_match_for, searchQuery),
        onSearchQueryChange = viewModel::setSearchQuery,
        searchQuery         = searchQuery,
        isSearchActive      = isSearchActive,
        onToggleSearch      = { viewModel.setSearchActive(!isSearchActive) },
        onBack              = onBackClick,
        onAdd               = onAddPatient,
        itemContent         = { patient, onClick ->
            PatientListItem(patient, onClick = onClick)
        },
        onItemClick         = { patient ->
            viewModel.saveSelectedPatient(patient, onSaved = {
                onPatientSelected(patient.name)
            })
        }
    )
}


@Composable
fun PatientListItem(patient: Patient, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ColorPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = patient.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patient.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(Res.string.p_id, patient.id),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(Res.string.select_patient),
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(ColorPrimary)
            )
        }
    }
}