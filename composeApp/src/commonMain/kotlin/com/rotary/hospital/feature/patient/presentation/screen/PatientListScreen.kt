package com.rotary.hospital.feature.patient.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.koin.compose.viewmodel.koinViewModel

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
    val error = (state as? PatientListState.Error)?.message
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()

    LaunchedEffect(phoneNumber) { viewModel.fetchPatients(phoneNumber) }

    SharedListScreen(
        title           = "Registered Patients",
        items           = patients,
        isLoading       = isLoading,
        errorMessage    = error,
        emptyMessage    = if (searchQuery.isEmpty()) "No patients found." else "No match for \"$searchQuery\"",
        onSearchQueryChange = viewModel::setSearchQuery,
        searchQuery     = searchQuery,
        isSearchActive  = isSearchActive,
        onToggleSearch  = { viewModel.setSearchActive(!isSearchActive) },
        onBack          = onBackClick,
        onAdd           = onAddPatient,
        itemContent     = { patient, onClick ->
            PatientListItem(patient, onClick = onClick)
        },
        onItemClick     = { patient ->
            viewModel.saveSelectedPatient(patient)
            onPatientSelected(patient.name)
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
                        text = "ID: ${patient.id}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Select patient",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(ColorPrimary.copy(alpha = 0.5f))
            )
        }
    }
}