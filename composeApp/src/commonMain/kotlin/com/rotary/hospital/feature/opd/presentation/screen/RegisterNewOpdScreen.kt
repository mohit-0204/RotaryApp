@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.toastController
import com.rotary.hospital.feature.opd.domain.model.Availability
import com.rotary.hospital.feature.opd.domain.model.Doctor
import com.rotary.hospital.feature.opd.domain.model.DoctorAvailability
import com.rotary.hospital.feature.opd.domain.model.InsertOpdResponse
import com.rotary.hospital.feature.opd.domain.model.Leave
import com.rotary.hospital.feature.opd.domain.model.Patient
import com.rotary.hospital.feature.opd.domain.model.Slot
import com.rotary.hospital.feature.opd.domain.model.Specialization
import com.rotary.hospital.feature.opd.presentation.viewmodel.DoctorAvailabilityViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.RegisterNewOpdViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.UiState
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Updated to match the new ViewModels (split UiState sources).
 * Keeps the original look & UX while modernizing state handling and improving robustness.
 */
@Composable
fun RegisterNewOpdScreen(
    paymentHandler: PaymentHandler,
    onSuccess: (InsertOpdResponse) -> Unit,
    onPending: () -> Unit,
    onFailure: () -> Unit,
    onBack: () -> Unit,
    patientId: String,
    patientName: String,
    mobileNumber: String,
    snackbarHostState: SnackbarHostState,
    viewModel: RegisterNewOpdViewModel = koinViewModel()
) {
    // --- Collect VM states ---
    val paymentState by viewModel.paymentState.collectAsState()
    val availabilityState by viewModel.availabilityState.collectAsState()
    val specializationsState by viewModel.specializationsState.collectAsState()
    val doctorsState by viewModel.doctorsState.collectAsState()
    val slotsState by viewModel.slotsState.collectAsState()

    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val selectedSpecialization by viewModel.selectedSpecialization.collectAsState()
    val selectedDoctor by viewModel.selectedDoctor.collectAsState()
    val selectedSlot by viewModel.selectedSlot.collectAsState()

    // --- Local bottom sheet plumbing (kept same UX) ---
    var sheetContent by remember { mutableStateOf<@Composable () -> Unit>({}) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Initial loads
    LaunchedEffect(Unit) {
        if (specializationsState !is UiState.Success) viewModel.fetchSpecializations()
        if (selectedPatient == null) {
            // In this flow patient comes from nav args; still reflect it in VM for consistency.
            viewModel.onSelectPatient(
                patient = Patient(
                    patientId = patientId,
                    patientName = patientName
                )
            )
        }
    }

    // Keep slots in sync with doctor selection (ViewModel also exposes helper methods)
    LaunchedEffect(selectedDoctor?.id) {
        selectedDoctor?.id?.let { viewModel.fetchSlots(it) }
    }

    // Handle payment result -> success handoff
    LaunchedEffect(paymentState) {
        when (paymentState) {
            is UiState.Success -> {
                Logger.d("Payment Result UI", "Payment successful")
                onSuccess((paymentState as UiState.Success).data)
                viewModel.resetPaymentState()
            }

            is UiState.Error -> {
                toastController.show((paymentState as UiState.Error).message)
                Logger.d(
                    "Payment Result UI",
                    "Payment failed: ${(paymentState as UiState.Error).message}"
                )
                onFailure() // Navigate to failed screen for both pending/failed payment
                viewModel.resetPaymentState()
            }

            is UiState.Loading -> {
                Logger.d("Payment Result UI", "Payment in progress")
            }

            UiState.Idle -> {}
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = { scope.launch { sheetState.hide() } },
                sheetState = sheetState,
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .align(Alignment.BottomCenter)
            ) {
                sheetContent()
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "New Appointment",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ColorPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ColorPrimary.copy(alpha = 0.8f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = White,
                        titleContentColor = Color.Black
                    )
                )
            },
            bottomBar = {
                val availability: Availability? = (availabilityState as? UiState.Success)?.data
                val canBook = availability?.available == true

                Column {
                    ElevatedButton(
                        onClick = {
                            val a = availability ?: return@ElevatedButton
                            if (!a.available) return@ElevatedButton

                            val total = (a.docCharges.toString().toDoubleOrNull() ?: 0.0) +
                                    (a.docOnlineCharges.toDoubleOrNull() ?: 0.0)

                            sheetContent = {
                                TermsSheet(total = total, onAccept = { amount ->
                                    if (amount <= 0.0) {
                                        scope.launch { sheetState.hide() }
                                        return@TermsSheet
                                    }

                                    viewModel.initiatePayment(
                                        paymentHandler = paymentHandler,
                                        mobileNumber = mobileNumber,
                                        amount = amount.toString(),
                                        patientId = patientId,
                                        patientName = patientName,
                                        doctorName = selectedDoctor?.name ?: "",
                                        doctorId = selectedDoctor?.id ?: "",
                                        durationPerPatient = a.docDurationPerPatient,
                                        docTimeFrom = a.docTimeFrom,
                                        opdType = selectedSpecialization ?: ""
                                    )
                                    scope.launch { sheetState.hide() }
                                })
                            }
                            scope.launch { sheetState.show() }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(WindowInsets.navigationBars.asPaddingValues())
                            .padding(horizontal = 8.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(2.dp),
                        enabled = canBook
                    ) {
                        Text("Book Appointment")
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Patient card (unchanged look)
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = lerp(
                                start = Color.White,
                                stop = ColorPrimary,
                                fraction = 0.1f
                            )
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = patientName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(text = "PID: $patientId", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }

                // Specialization selector
                item {
                    Spacer(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth())
                    Text(
                        text = "Specialization:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth())

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                sheetContent = {
                                    val list =
                                        (specializationsState as? UiState.Success<List<Specialization>>)?.data
                                            ?: emptyList()
                                    SpecializationSheet(
                                        selected = selectedSpecialization ?: "",
                                        specializations = list
                                    ) { spec ->
                                        viewModel.onSelectSpecialization(spec)
                                        scope.launch { sheetState.hide() }
                                    }
                                }
                                scope.launch { sheetState.show() }
                            }
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(end = 40.dp)) {
                            Text(
                                text = (selectedSpecialization
                                    ?: "").ifEmpty { "Select Specialization" },
                                color = if ((selectedSpecialization
                                        ?: "").isEmpty()
                                ) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Doctor selector
                    Text(
                        text = "Doctor:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth())
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable(enabled = !selectedSpecialization.isNullOrEmpty()) {
                                sheetContent = {
                                    val list =
                                        (doctorsState as? UiState.Success<List<Doctor>>)?.data
                                            ?: emptyList()
                                    DoctorSheet(
                                        selected = selectedDoctor,
                                        doctors = list
                                    ) { doctor ->
                                        if (doctor != null) viewModel.onSelectDoctor(doctor)
                                        scope.launch { sheetState.hide() }
                                    }
                                }
                                scope.launch { sheetState.show() }
                            }
                            .padding(12.dp)
                    ) {
                        val isEnabled = !selectedSpecialization.isNullOrEmpty()
                        Column(modifier = Modifier.padding(end = 40.dp)) {
                            Text(
                                text = selectedDoctor?.name ?: "Select Doctor",
                                color = if (!isEnabled) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.38f
                                ) else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                            tint = if (!isEnabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Slot selector
                    Text(
                        text = "Slot:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth())
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable(enabled = selectedDoctor != null) {
                                sheetContent = {
                                    val list = (slotsState as? UiState.Success<List<Slot>>)?.data
                                        ?: emptyList()
                                    SlotSheet(
                                        selected = selectedSlot,
                                        slots = list,
                                        onSelect = { slot ->
                                            viewModel.onSelectSlot(slot)
                                            scope.launch { sheetState.hide() }
                                        }
                                    )
                                }
                                scope.launch { sheetState.show() }
                            }
                            .padding(12.dp)
                    ) {
                        val isEnabled = selectedDoctor != null
                        Column(modifier = Modifier.padding(end = 40.dp)) {
                            val slotLabel = selectedSlot?.let { "${it.timeFrom} - ${it.timeTo}" }
                                ?: "Select Slot Timing"
                            Text(
                                text = slotLabel,
                                color = if (!isEnabled) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.38f
                                ) else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                            tint = if (!isEnabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Availability details + loading/error handling (scoped to availability state)
                item {
                    when (availabilityState) {
                        is UiState.Loading -> Box(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                Modifier.align(Alignment.Center),
                                color = ColorPrimary
                            )
                        }

                        is UiState.Error -> Text(
                            text = (availabilityState as UiState.Error).message,
                            color = Color.Red,
                            fontSize = 16.sp
                        )

                        is UiState.Success -> {
                            val availability =
                                (availabilityState as UiState.Success<Availability>).data
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = lerp(
                                        start = Color.White,
                                        stop = ColorPrimary,
                                        fraction = 0.1f
                                    )
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Appointment Details",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (availability.available) "Available" else "Slots Full",
                                            modifier = Modifier
                                                .background(
                                                    if (availability.available) Color.Green else Color.Red,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = Color.White,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Room no:", fontSize = 16.sp)
                                        Text(selectedDoctor?.opdRoom ?: "", fontSize = 16.sp)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("OPD Charges:", fontSize = 16.sp)
                                        Text("${availability.docCharges} Rs", fontSize = 16.sp)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Online Charges:", fontSize = 16.sp)
                                        Text(
                                            "${availability.docOnlineCharges} Rs",
                                            fontSize = 16.sp
                                        )
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Expected Wait Time:", fontSize = 16.sp)
                                        Text(
                                            "${availability.approximateTime} minutes",
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        UiState.Idle -> {}
                    }
                }

                // Payment state handling
                item {
                    when (paymentState) {
                        is UiState.Loading -> Box(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                Modifier.align(Alignment.Center),
                                color = ColorPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Processing Payment...",
                                fontSize = 16.sp,
                                color = ColorPrimary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        is UiState.Error -> Text(
                            text = (paymentState as UiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun SpecializationSheet(
    selected: String,
    specializations: List<Specialization>,
    onSelect: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Specialization") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        val filteredSpecs =
            specializations.filter { it.data.contains(searchQuery, ignoreCase = true) }

        if (filteredSpecs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No specializations found",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn {
                items(filteredSpecs) { spec ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelect(spec.data) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = spec.data,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            color = if (spec.data == selected) Color(0xFF00BCD4) else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorSheet(
    selected: Doctor?,
    doctors: List<Doctor>,
    onSelect: (Doctor?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Doctor") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        val filteredDoctors = doctors.filter { it.name.contains(searchQuery, ignoreCase = true) }

        if (filteredDoctors.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No doctors found",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn {
                items(filteredDoctors) { doctor ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelect(doctor) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = doctor.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            color = if (doctor == selected) Color(0xFF00BCD4) else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SlotSheet(
    selected: Slot?,
    slots: List<Slot>,
    onSelect: (Slot) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (slots.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    color = Color(0xFF00BCD4),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(slots) { slot ->
                    Button(
                        onClick = { onSelect(slot) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("${slot.timeFrom} - ${slot.timeTo}", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TermsSheet(total: Double, onAccept: (Double) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Terms & Conditions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Total Charges: $total Rs", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onAccept(0.0) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) { Text("Cancel", color = Color.White) }
            Button(
                onClick = { onAccept(total) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) { Text("Accept", color = Color.White) }
        }
    }
}

@Composable
fun DoctorAvailabilityScreen(
    doctorId: String,
    onBack: () -> Unit,
    viewModel: DoctorAvailabilityViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDoctorAvailability(doctorId)
    }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Doctor Availability",
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) { Text("Back", color = ColorPrimary) }
            }
            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is UiState.Loading -> CircularProgressIndicator(color = ColorPrimary)
                is UiState.Success -> {
                    val (availability, leaves) = (state as UiState.Success<Pair<List<DoctorAvailability>, List<Leave>>>).data
                    LazyColumn {
                        items(availability) { avail ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Days: ${avail.docDays}",
                                        color = ColorPrimary,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Time: ${avail.docTimeFrom} - ${avail.docTimeTo}",
                                        color = ColorPrimary,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                        if (leaves.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Leaves",
                                    color = ColorPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                            items(leaves) { leave ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "From: ${leave.cancelDate}",
                                            color = ColorPrimary,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "To: ${leave.cancelDateTo}",
                                            color = ColorPrimary,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Text(
                        text = (state as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                }

                UiState.Idle -> {}
            }
        }
    }
}
