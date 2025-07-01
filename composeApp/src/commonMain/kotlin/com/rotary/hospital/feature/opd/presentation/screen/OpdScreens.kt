package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.screen.SharedListScreen
import com.rotary.hospital.feature.opd.domain.model.Availability
import com.rotary.hospital.feature.opd.domain.model.Doctor
import com.rotary.hospital.feature.opd.domain.model.InsertOpdResponse
import com.rotary.hospital.feature.opd.domain.model.Opd
import com.rotary.hospital.feature.opd.domain.model.Patient
import com.rotary.hospital.feature.opd.domain.model.PaymentRequest
import com.rotary.hospital.feature.opd.presentation.screen.components.OpdListItem
import com.rotary.hospital.feature.opd.presentation.viewmodel.DoctorAvailabilityState
import com.rotary.hospital.feature.opd.presentation.viewmodel.DoctorAvailabilityViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPatientListState
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPatientListViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPaymentFailedState
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPaymentFailedViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPaymentPendingState
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPaymentPendingViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPaymentSuccessState
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPaymentSuccessViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.RegisterNewOpdState
import com.rotary.hospital.feature.opd.presentation.viewmodel.RegisterNewOpdViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.RegisteredOpdsState
import com.rotary.hospital.feature.opd.presentation.viewmodel.RegisteredOpdsViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.SelectedOpdDetailsState
import com.rotary.hospital.feature.opd.presentation.viewmodel.SelectedOpdDetailsViewModel
import com.rotary.hospital.feature.patient.presentation.screen.PatientListItem
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisteredOpdsScreen(
    onOpdClick: (String) -> Unit,
    onAddNew: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: RegisteredOpdsViewModel = koinViewModel(),
    preferences: PreferencesManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    var mobileNumber by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").collect { savedMobile ->
            if (savedMobile.isNotBlank()) {
                mobileNumber = savedMobile
                viewModel.fetchOpds(savedMobile)
            } else {
                Logger.e("RegisteredOpdsScreen", "Mobile number not found")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Booked OPDs",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
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
                onClick = onAddNew,
                containerColor = ColorPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add OPD", tint = Color.White)
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val currentState = state) {
                is RegisteredOpdsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is RegisteredOpdsState.Error -> {
                    Text(
                        text = currentState.message,
                        color = ErrorRed,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RegisteredOpdsState.Success -> {
                    val opds = currentState.opds
                    if (opds.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No OPDs booked.", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onAddNew) {
                                Text("Register New OPD")
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(opds) { opd ->
                                OpdListItem(opd = opd, onClick = { onOpdClick(opd.opdId) })
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
fun OpdPatientListScreen(
    onPatientClick: (String, String) -> Unit,
    onBack         : () -> Unit,
    viewModel      : OpdPatientListViewModel = koinViewModel(),
    preferences    : PreferencesManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val patients = (state as? OpdPatientListState.Success)?.patients.orEmpty()
    val isLoading = state is OpdPatientListState.Loading
    val error = (state as? OpdPatientListState.Error)?.message

    LaunchedEffect(Unit) {
        preferences.getString(PreferenceKeys.MOBILE_NUMBER, "")
            .collect { pref ->
                if (pref.isNotBlank()) viewModel.fetchPatients(pref)
            }
    }

    SharedListScreen(
        title           = "Select Patient for OPD",
        items           = patients,
        isLoading       = isLoading,
        errorMessage    = error,
        emptyMessage    = "No patients registered",
        onSearchQueryChange = null,   // no search here
        isSearchActive  = false,
        onToggleSearch  = null,
        onBack          = onBack,
        onAdd           = null,       // no FAB here
        itemContent     = { opdPatient, onClick ->
            PatientListItem(patient = com.rotary.hospital.core.data.model.Patient(opdPatient.patientId, opdPatient.patientName,""), onClick = onClick)
        },
        onItemClick     = { opdPatient ->
            onPatientClick(opdPatient.patientId, opdPatient.patientName)
        }
    )
}


@Composable
fun RegisterNewOpdScreen(
    onPaymentInitiated: (PaymentRequest) -> Unit,
    onSuccess: (InsertOpdResponse) -> Unit,
    onBack: () -> Unit,
    patientId: String,
    patientName: String,
    mobileNumber: String,
    viewModel: RegisterNewOpdViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedSpecialization by remember { mutableStateOf("") }
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }
    var selectedSlot by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf<Availability?>(null) }
    var expandedSpec by remember { mutableStateOf(false) }
    var expandedDoctor by remember { mutableStateOf(false) }
    var expandedSlot by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchSpecializations()
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
                    text = "Book New OPD",
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) {
                    Text("Back", color = ColorPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (state) {
                is RegisterNewOpdState.Loading -> CircularProgressIndicator(color = ColorPrimary)
                is RegisterNewOpdState.Error -> {
                    Text(
                        text = (state as RegisterNewOpdState.Error).message,
                        color = ErrorRed,
                        fontSize = 16.sp
                    )
                }

                is RegisterNewOpdState.SpecializationsLoaded -> {
                    val specs = (state as RegisterNewOpdState.SpecializationsLoaded).specializations
                    Box {
                        OutlinedTextField(
                            value = selectedSpecialization,
                            onValueChange = {},
                            label = { Text("Specialization") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        DropdownMenu(
                            expanded = expandedSpec,
                            onDismissRequest = { expandedSpec = false }
                        ) {
                            specs.forEach { spec ->
                                DropdownMenuItem(
                                    text = { Text(spec.data) },
                                    onClick = {
                                        selectedSpecialization = spec.data
                                        expandedSpec = false
                                        viewModel.fetchDoctors(spec.data)
                                    }
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .background(White.copy(alpha = 0f))
                                .clickable { expandedSpec = true }
                        )
                    }
                }

                is RegisterNewOpdState.DoctorsLoaded -> {
                    val doctors = (state as RegisterNewOpdState.DoctorsLoaded).doctors
                    Box {
                        OutlinedTextField(
                            value = selectedDoctor?.name ?: "",
                            onValueChange = {},
                            label = { Text("Doctor") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        DropdownMenu(
                            expanded = expandedDoctor,
                            onDismissRequest = { expandedDoctor = false }
                        ) {
                            doctors.forEach { doctor ->
                                DropdownMenuItem(
                                    text = { Text(doctor.name) },
                                    onClick = {
                                        selectedDoctor = doctor
                                        expandedDoctor = false
                                        viewModel.fetchSlots(doctor.id)
                                    }
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .background(White.copy(alpha = 0f))
                                .clickable { expandedDoctor = true }
                        )
                    }
                }

                is RegisterNewOpdState.SlotsLoaded -> {
                    val slots = (state as RegisterNewOpdState.SlotsLoaded).slots
                    Box {
                        OutlinedTextField(
                            value = selectedSlot,
                            onValueChange = {},
                            label = { Text("Slot") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        DropdownMenu(
                            expanded = expandedSlot,
                            onDismissRequest = { expandedSlot = false }
                        ) {
                            slots.forEach { slot ->
                                DropdownMenuItem(
                                    text = { Text("${slot.timeFrom} - ${slot.timeTo}") },
                                    onClick = {
                                        selectedSlot = slot.timeFrom
                                        expandedSlot = false
                                        viewModel.fetchAvailability(
                                            selectedDoctor!!.id,
                                            slot.timeFrom
                                        )
                                    }
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .background(White.copy(alpha = 0f))
                                .clickable { expandedSlot = true }
                        )
                    }
                }

                is RegisterNewOpdState.AvailabilityLoaded -> {
                    availability = (state as RegisterNewOpdState.AvailabilityLoaded).availability
                    if (availability != null) {
                        Column {
                            Text(
                                text = "Charges: ${availability!!.docCharges}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Time: ${availability!!.docTimeFrom} - ${availability!!.docTimeTo}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.initiatePayment(
                                        mobileNumber = mobileNumber,
                                        amount = availability!!.docCharges,
                                        patientId = patientId,
                                        patientName = patientName,
                                        doctorName = selectedDoctor!!.name
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorPrimary,
                                    contentColor = White
                                )
                            ) {
                                Text("Proceed to Payment")
                            }
                        }
                    }
                }

                is RegisterNewOpdState.PaymentInitiated -> {
                    val payment = (state as RegisterNewOpdState.PaymentInitiated).payment
                    if (payment != null) {
                        LaunchedEffect(Unit) {
                            onPaymentInitiated(payment)
                        }
                    }
                }

                is RegisterNewOpdState.Success -> {
                    val response = (state as RegisterNewOpdState.Success).response
                    LaunchedEffect(Unit) {
                        onSuccess(response)
                    }
                }

                is RegisterNewOpdState.Idle -> {}
            }
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
        viewModel.fetchAvailability(doctorId)
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
                TextButton(onClick = onBack) {
                    Text("Back", color = ColorPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (state) {
                is DoctorAvailabilityState.Loading -> CircularProgressIndicator(color = ColorPrimary)
                is DoctorAvailabilityState.Success -> {
                    val (availability, leaves) = (state as DoctorAvailabilityState.Success)
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

                is DoctorAvailabilityState.Error -> {
                    Text(
                        text = (state as DoctorAvailabilityState.Error).message,
                        color = ErrorRed,
                        fontSize = 16.sp
                    )
                }

                is DoctorAvailabilityState.Idle -> {}
            }
        }
    }
}

@Composable
fun OpdPaymentSuccessScreen(
    merchantTransactionId: String,
    onShareScreenshot: () -> Unit,
    onBack: () -> Unit,
    viewModel: OpdPaymentSuccessViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPaymentStatus(merchantTransactionId)
    }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment Success",
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) {
                    Text("Back", color = ColorPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (state) {
                is OpdPaymentSuccessState.Loading -> CircularProgressIndicator(color = ColorPrimary)
                is OpdPaymentSuccessState.Success -> {
                    val status = (state as OpdPaymentSuccessState.Success).paymentStatus
                    Text(
                        text = "Transaction ID: ${status.transactionId}",
                        color = ColorPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = status.message,
                        color = ColorPrimary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onShareScreenshot,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary,
                            contentColor = White
                        )
                    ) {
                        Text("Share Screenshot")
                    }
                }

                is OpdPaymentSuccessState.Error -> {
                    Text(
                        text = (state as OpdPaymentSuccessState.Error).message,
                        color = ErrorRed,
                        fontSize = 16.sp
                    )
                }

                is OpdPaymentSuccessState.Idle -> {}
            }
        }
    }
}

@Composable
fun OpdPaymentPendingScreen(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    viewModel: OpdPaymentPendingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updatePendingState(message)
    }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment Pending",
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) {
                    Text("Back", color = ColorPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (state) {
                is OpdPaymentPendingState.Pending -> {
                    Text(
                        text = (state as OpdPaymentPendingState.Pending).message,
                        color = ColorPrimary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary,
                            contentColor = White
                        )
                    ) {
                        Text("Retry")
                    }
                }

                is OpdPaymentPendingState.Idle -> {}
            }
        }
    }
}

@Composable
fun OpdPaymentFailedScreen(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    viewModel: OpdPaymentFailedViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateFailedState(message)
    }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment Failed",
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) {
                    Text("Back", color = ColorPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (state) {
                is OpdPaymentFailedState.Failed -> {
                    Text(
                        text = (state as OpdPaymentFailedState.Failed).message ?: "Payment failed",
                        color = ErrorRed,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary,
                            contentColor = White
                        )
                    ) {
                        Text("Retry")
                    }
                }

                is OpdPaymentFailedState.Idle -> {}
            }
        }
    }
}

@Composable
fun SelectedOpdDetailsScreen(
    opdId: String,
    onBack: () -> Unit,
    viewModel: SelectedOpdDetailsViewModel = koinViewModel(),
    preferences: PreferencesManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    var mobileNumber by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").collect { savedMobile ->
            if (savedMobile.isNotBlank()) {
                mobileNumber = savedMobile
                viewModel.fetchOpdDetails(savedMobile, opdId)
            }
        }
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
                    text = "OPD Details",
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) {
                    Text("Back", color = ColorPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (state) {
                is SelectedOpdDetailsState.Loading -> CircularProgressIndicator(color = ColorPrimary)
                is SelectedOpdDetailsState.Success -> {
                    val opd = (state as SelectedOpdDetailsState.Success).opd
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
                                text = "OPD ID: ${opd.opdId}",
                                color = ColorPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Type: ${opd.opdType}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Patient: ${opd.patientName}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Doctor: ${opd.doctor}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Date: ${opd.date}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                is SelectedOpdDetailsState.Error -> {
                    Text(
                        text = (state as SelectedOpdDetailsState.Error).message,
                        color = ErrorRed,
                        fontSize = 16.sp
                    )
                }

                is SelectedOpdDetailsState.Idle -> {}
            }
        }
    }
}

@Composable
private fun PatientItem(
    patient: Patient,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = patient.patientName,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "ID: ${patient.patientId}",
                color = ColorPrimary,
                fontSize = 14.sp
            )
        }
    }
}