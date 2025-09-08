@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.ui.toastController
import com.rotary.hospital.feature.opd.domain.model.Availability
import com.rotary.hospital.feature.opd.domain.model.Doctor
import com.rotary.hospital.feature.opd.domain.model.Slot
import com.rotary.hospital.feature.opd.domain.model.Specialization
import com.rotary.hospital.feature.opd.domain.usecase.PaymentFlowResult
import com.rotary.hospital.feature.opd.presentation.model.TransactionDetails
import com.rotary.hospital.feature.opd.presentation.viewmodel.RegisterNewOpdViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.UiState
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OpdRegistrationScreen(
    paymentHandler: PaymentHandler,
    onPaymentResult: (TransactionDetails) -> Unit,
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

    val selectedSpecialization by viewModel.selectedSpecialization.collectAsState()
    val selectedDoctor by viewModel.selectedDoctor.collectAsState()
    val selectedSlot by viewModel.selectedSlot.collectAsState()

    // --- Local bottom sheet state ---
    var sheetContent by remember { mutableStateOf<@Composable () -> Unit>({}) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
//        animationSpec = androidx.compose.animation.core.tween(0)
    )

    // Initial loads
    LaunchedEffect(Unit) {
        if (specializationsState !is UiState.Success) viewModel.fetchSpecializations()
    }

    // Keep slots in sync with doctor selection
    LaunchedEffect(selectedDoctor?.id) {
        selectedDoctor?.id?.let { viewModel.fetchSlots(it) }
    }

    // Handle payment result -> success handoff
    LaunchedEffect(paymentState) {
        when (paymentState) {
            is UiState.Idle -> { /* show nothing */
            }

            is UiState.Loading -> { /* show loading state */
            }

            is UiState.Success -> {
                when (val flowResult = (paymentState as UiState.Success<PaymentFlowResult>).data) {
                    is PaymentFlowResult.Success -> onPaymentResult(flowResult.successResponse)
                    is PaymentFlowResult.Pending -> onPaymentResult(flowResult.pendingResponse)
                    is PaymentFlowResult.Failed -> onPaymentResult(flowResult.failedResponse)
                    is PaymentFlowResult.Cancelled -> {
                        viewModel.resetPaymentState()
                        toastController.show("Payment Cancelled by User")
                    }

                    is PaymentFlowResult.Error -> {
                        viewModel.resetPaymentState()
                        toastController.show(flowResult.message)
                    }

                    is PaymentFlowResult.Loading -> TODO()
                }
            }

            is UiState.Error -> {
                Logger.e("PaymentError", (paymentState as UiState.Error).message)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = { scope.launch { sheetState.hide() } },
                sheetState = sheetState,
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .align(Alignment.BottomCenter),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                sheetContent()
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "OPD Registration",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            bottomBar = {
                val availability: Availability? = (availabilityState as? UiState.Success)?.data
                val canBook = availability?.available == true

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            val a = availability ?: return@Button
                            if (!a.available) return@Button

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
                                        roomNumber = selectedDoctor?.opdRoom ?: "",
                                        specialization = selectedSpecialization ?: "",
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
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = canBook
                    ) {
                        Text(
                            "BOOK APPOINTMENT",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Patient Info Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(32.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Patient Information",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Divider()
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Patient ID", color = Color(0xFF6C757D))
                            Text(patientId, fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Patient Name", color = Color(0xFF6C757D))
                            Text(patientName, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Dropdown style fields
                LabeledDropdown(
                    label = "Select Specialization",
                    value = selectedSpecialization ?: "Select Specialization",
                    onClick = {
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
                )

                LabeledDropdown(
                    label = "Select Doctor Name",
                    value = selectedDoctor?.name ?: "Select Doctor",
                    enabled = !selectedSpecialization.isNullOrEmpty(),
                    onClick = {
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
                )

                val slotLabel = selectedSlot?.let { "${it.timeFrom} - ${it.timeTo}" }
                    ?: "Select Slot Timing"

                LabeledDropdown(
                    label = "Select Time Slots",
                    value = slotLabel,
                    enabled = selectedDoctor != null,
                    onClick = {
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
                )

                // Availability details + loading/error handling (scoped to availability state)
                when (availabilityState) {
                    is UiState.Loading -> Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CircularProgressIndicator(
                            Modifier.align(Alignment.Center),
                            color = ColorPrimary
                        )
                    }

                    is UiState.Error -> HtmlStyleAvailabilityAlert(
                        title = "Error",
                        message = (availabilityState as UiState.Error).message
                    )

                    is UiState.Success -> {
                        val availability =
                            (availabilityState as UiState.Success<Availability>).data
                        if (!availability.available) {
                            HtmlStyleAvailabilityAlert() // Shows default "Not available today" message
                        } else {
                            // You can add a success state card here if you wish,
                            // for now, we just don't show the alert.
                        }
                    }

                    is UiState.Idle -> {
                        // Show nothing or a prompt to select a doctor/slot
                    }
                }
            }
        }
    }
}


@Composable
private fun LabeledDropdown(
    label: String,
    value: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val textColor = if (enabled) Color(0xFF212529) else Color(0xFF212529).copy(alpha = 0.5f)
    val iconTint = if (enabled) Color(0xFF6C757D) else Color(0xFF6C757D).copy(alpha = 0.5f)

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0F766E),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFDEE2E6), RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(value, color = textColor)
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = iconTint
                )
            }
        }
    }
}

@Composable
fun HtmlStyleAvailabilityAlert(
    title: String = "Not available today.",
    message: String = "This App allows OPD Registration only for current day. Check availability and try on next available day."
) {
    val bg = Color(0xFFFEE2E2)      // tailwind red-100 (kept to match mock)
    val leftBar = Color(0xFFEF4444) // tailwind red-500
    val textColor = Color(0xFFB91C1C) // tailwind red-700
    val corner = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(corner)
            .background(bg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(leftBar)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = textColor
                )
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