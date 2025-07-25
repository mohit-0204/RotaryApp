@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.core.payment.PaymentResult
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.Black
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.screen.SharedListScreen
import com.rotary.hospital.core.ui.toastController
import com.rotary.hospital.feature.auth.presentation.viewmodel.LoginState
import com.rotary.hospital.feature.opd.domain.model.Availability
import com.rotary.hospital.feature.opd.domain.model.Doctor
import com.rotary.hospital.feature.opd.domain.model.InsertOpdResponse
import com.rotary.hospital.feature.opd.domain.model.Opd
import com.rotary.hospital.feature.opd.domain.model.Patient
import com.rotary.hospital.feature.opd.domain.model.PaymentRequest
import com.rotary.hospital.feature.opd.domain.model.Slot
import com.rotary.hospital.feature.opd.domain.model.Specialization
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
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

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
                }, navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                }, actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, titleContentColor = Color.Black
                )
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNew, containerColor = ColorPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add OPD", tint = Color.White)
            }
        }, containerColor = Color.White
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
        onAdd = null,       // no FAB here
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

@Composable
fun RegisterNewOpdScreen(
    paymentHandler: PaymentHandler?,
    onSuccess: (InsertOpdResponse) -> Unit,
    onBack: () -> Unit,
    patientId: String,
    patientName: String,
    mobileNumber: String,
    viewModel: RegisterNewOpdViewModel = koinViewModel(),
    paymentViewModel: OpdPaymentSuccessViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val paymentState by paymentViewModel.state.collectAsState()

    val availability = state.availability
    var selectedSpecialization by remember { mutableStateOf("") }
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }
    var selectedSlot by remember { mutableStateOf("") }
    var sheetContent by remember { mutableStateOf<@Composable () -> Unit>({}) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        if (state.specializations.isEmpty()) {
            viewModel.fetchSpecializations()
        }
    }


    LaunchedEffect(selectedDoctor) {
        selectedDoctor?.id?.let { viewModel.fetchSlots(it) }
        selectedSlot = ""
    }

    Box(Modifier.fillMaxSize()) {
        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = { scope.launch { sheetState.hide() } },
                sheetState = sheetState,
                modifier = Modifier.fillMaxHeight(0.7f).align(Alignment.BottomCenter)
            ) {
                sheetContent()
            }
        }

        Scaffold(
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
                Column {
                    ElevatedButton(
                        onClick = {
                            availability?.let { availability ->
                                if (availability.available) {
                                    val total = availability.docCharges.toDoubleOrNull()
                                        ?.plus(
                                            availability.docOnlineCharges.toDoubleOrNull() ?: 0.0
                                        ) ?: 0.0
                                    sheetContent = {
                                        TermsSheet(total = total, onAccept = { amount ->
                                            if (amount > 0) {
                                                viewModel.initiatePayment(
                                                    mobileNumber = mobileNumber,
                                                    amount = amount.toString(),
                                                    patientId = patientId,
                                                    patientName = patientName,
                                                    doctorName = selectedDoctor?.name ?: ""
                                                )
                                            }
                                            scope.launch { sheetState.hide() }
                                        })

                                    }
                                    scope.launch { sheetState.show() }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(WindowInsets.navigationBars.asPaddingValues())
                            .padding(horizontal = 8.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(2.dp),
                        enabled = availability?.available == true
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
                // ... No changes to patient card, selection boxes ...
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = lerp(
                                start = Color.White,
                                stop = ColorPrimary,
                                fraction = 0.1f // means 20% toward your color, 80% white = light tint
                            )
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = patientName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(text = "PID: $patientId", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
                item {

                    Spacer(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth())
                    Text(
                        text = "Specialization:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth())

                    // Custom Box for Specialization
                    Box(
                        modifier = Modifier.fillMaxWidth().border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        ).clickable {
                            sheetContent = {
                                SpecializationSheet(
                                    selected = selectedSpecialization,
                                    specializations = state.specializations
                                ) { spec ->
                                    selectedSpecialization = spec
                                    selectedDoctor = null
                                    viewModel.fetchDoctors(spec)
                                    scope.launch { sheetState.hide() }
                                }
                            }
                            scope.launch {
                                sheetState.show()
                            }
                        }.padding(12.dp)
                    ) {


                        Column(modifier = Modifier.padding(end = 40.dp)) {
                            Text(
                                text = selectedSpecialization.ifEmpty { "Select Specialization" },
                                color = if (selectedSpecialization.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
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


                    // Custom Box for Doctor
                    Text(
                        text = "Doctor:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth())
                    Box(
                        modifier = Modifier.fillMaxWidth().border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        ).clickable(
                            enabled = selectedSpecialization.isNotEmpty()
                        ) {
                            if (selectedSpecialization.isNotEmpty()) {
                                sheetContent = {
                                    DoctorSheet(
                                        selected = selectedDoctor,
                                        doctors = state.doctors
                                    ) { doctor ->
                                        selectedDoctor = doctor
                                        viewModel.fetchSlots(doctor?.id ?: "")
                                        scope.launch { sheetState.hide() }
                                    }
                                }
                                scope.launch {
                                    sheetState.show()
                                }
                            }
                        }.padding(12.dp)
                    ) {
                        val isEnabled = selectedSpecialization.isNotEmpty()
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
                    // Custom Box for Slots
                    Text(
                        text = "Slot:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth())
                    Box(
                        modifier = Modifier.fillMaxWidth().border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        ).clickable(
                            enabled = selectedDoctor != null
                        ) {
                            if (selectedDoctor != null) {
                                sheetContent = {
                                    SlotSheet(
                                        selected = selectedSlot,
                                        slots = state.slots,
                                        onSelect = { slot ->
                                            selectedSlot = slot
                                            viewModel.fetchAvailability(selectedDoctor!!.id, slot)
                                            scope.launch { sheetState.hide() }
                                        }
                                    )
                                }
                                scope.launch {
                                    sheetState.show()
                                }
                            }
                        }.padding(12.dp)
                    ) {
                        val isEnabled = selectedDoctor != null
                        Column(modifier = Modifier.padding(end = 40.dp)) {
                            Text(
                                text = selectedSlot.ifEmpty { "Select Slot Timing" },
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

                item {
                    when {
                        state.isLoading -> Box(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                Modifier.align(Alignment.Center),
                                color = ColorPrimary
                            )
                        }

                        state.errorMessage != null -> Text(
                            text = state.errorMessage ?: "",
                            color = Color.Red,
                            fontSize = 16.sp
                        )

                        availability != null -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = lerp(
                                        start = Color.White,
                                        stop = ColorPrimary,
                                        fraction = 0.1f // means 20% toward your color, 80% white = light tint
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
                                            modifier = Modifier.background(
                                                if (availability.available) Color.Green else Color.Red,
                                                shape = RoundedCornerShape(8.dp)
                                            ).padding(horizontal = 8.dp, vertical = 4.dp),
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
                    }
                }
            }

            // ✅ Payment Trigger
            LaunchedEffect(state.payment) {
                state.payment?.let { payment ->
                    paymentHandler?.startPayment(
                        payment.payloadBase64,
                        payment.checksum,
                        payment.apiEndPoint
                    ) { result ->
                        when (result) {
                            PaymentResult.Cancelled -> toastController.show("Payment got canceled")
                            is PaymentResult.Failure -> toastController.show("Payment failed")
                            is PaymentResult.Success -> {
                                paymentViewModel.fetchPaymentStatus(payment.merchantTransactionId)
                            }
                        }
                    }
                }
            }

            // ✅ OPD Insert after Payment Success
            LaunchedEffect(paymentState) {
                val success =
                    paymentState as? OpdPaymentSuccessState.Success ?: return@LaunchedEffect
                availability?.let {
                    viewModel.insertOpd(
                        patientId = patientId,
                        patientName = patientName,
                        mobileNumber = mobileNumber,
                        doctorName = selectedDoctor?.name.orEmpty(),
                        doctorId = selectedDoctor?.id.orEmpty(),
                        opdAmount = it.docCharges,
                        durationPerPatient = it.docDurationPerPatient,
                        docTimeFrom = it.docTimeFrom,
                        opdType = selectedSpecialization,
                        transactionId = success.paymentStatus.transactionId,
                        paymentId = success.paymentStatus.transactionId,
                        orderId = success.paymentStatus.transactionId,
                        status = success.paymentStatus.response,
                        message = success.paymentStatus.message
                    )
                }
            }

            // ✅ Final success response trigger
            LaunchedEffect(state.response) {
                state.response?.let {
                    onSuccess(it)
                    viewModel.clearResponse()
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
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        val filteredSpecs = specializations.filter {
            it.data.contains(searchQuery, ignoreCase = true)
        }

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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .clickable { onSelect(spec.data) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = spec.data,
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
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
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        val filteredDoctors = doctors.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .clickable { onSelect(doctor) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = doctor.name,
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
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
    selected: String,
    slots: List<Slot>,
    onSelect: (String) -> Unit
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
                        onClick = { onSelect(slot.timeFrom) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
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
        Text(
            text = "Terms & Conditions", fontSize = 20.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Total Charges: $total Rs", fontSize = 16.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onAccept(0.0) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancel", color = Color.White)
            }
            Button(
                onClick = { onAccept(total) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) {
                Text("Accept", color = Color.White)
            }
        }
    }
}

@Composable
fun DoctorAvailabilityScreen(
    doctorId: String, onBack: () -> Unit, viewModel: DoctorAvailabilityViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAvailability(doctorId)
    }

    AppTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
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
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
//        viewModel.fetchPaymentStatus(merchantTransactionId)
    }

    AppTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                        text = status.message, color = ColorPrimary, fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onShareScreenshot, colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary, contentColor = White
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
                is OpdPaymentSuccessState.Pending -> TODO()
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
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                        onClick = onRetry, colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary, contentColor = White
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
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                        onClick = onRetry, colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary, contentColor = White
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
            modifier = Modifier.fillMaxSize().padding(16.dp)
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                                text = "Date: ${opd.date}", color = ColorPrimary, fontSize = 16.sp
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
    patient: Patient, onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(
                text = patient.patientName,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "ID: ${patient.patientId}", color = ColorPrimary, fontSize = 14.sp
            )
        }
    }
}