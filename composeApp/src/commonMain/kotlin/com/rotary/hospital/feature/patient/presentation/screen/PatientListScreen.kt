package com.rotary.hospital.feature.patient.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.data.model.Patient
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientListState
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientListViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    phoneNumber: String,
    onAddPatient: () -> Unit,
    onBackClick: () -> Unit,
    onPatientSelected: (String) -> Unit,
    viewModel: PatientListViewModel = koinViewModel()
) {
    LaunchedEffect(phoneNumber) {
        viewModel.fetchPatients(phoneNumber)
    }

    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search patients...") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = ColorPrimary,
                                unfocusedIndicatorColor = Color.Gray,
                                cursorColor = ColorPrimary
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = ColorPrimary.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        Text(
                            "Registered Patients",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ColorPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSearchActive) {
                            viewModel.setSearchActive(false)
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isSearchActive) "Cancel search" else "Back",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { viewModel.setSearchActive(true) }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search patients",
                                tint = ColorPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPatient,
                containerColor = ColorPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Patient", tint = Color.White)
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val currentState = state) {
                is PatientListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PatientListState.Error -> {
                    Text(
                        text = currentState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is PatientListState.Success -> {
                    val patients = currentState.patients
                    if (patients.isEmpty() && searchQuery.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No patients found.", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onAddPatient) {
                                Text("Register New Patient")
                            }
                        }
                    } else if (patients.isEmpty()) {
                        Text(
                            text = "No patients match your search.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(patients) { patient ->
                                PatientListItem(
                                    patient = patient,
                                    onClick = {
                                        viewModel.saveSelectedPatient(patient)
                                        onPatientSelected(patient.name)
                                    }
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
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