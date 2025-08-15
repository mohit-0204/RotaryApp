package com.rotary.hospital.feature.patient.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.component.InputField
import com.rotary.hospital.core.ui.toastController
import com.rotary.hospital.feature.patient.presentation.viewmodel.Gender
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientProfileState
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientProfileViewModel
import com.rotary.hospital.feature.patient.presentation.viewmodel.Relation
import org.koin.compose.viewmodel.koinViewModel
import appicon.IconDrop
import appicon.IconGuardian
import appicon.IconMale
import appicon.IconFemale
import appicon.IconOther
import com.rotary.hospital.core.common.appicon.IconCity
import com.rotary.hospital.core.common.appicon.IconMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    patientId: String,
    onBack: () -> Unit,
    onSave: (String) -> Unit,
    viewModel: PatientProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    var bloodExpanded by remember { mutableStateOf(false) }
    var updateButtonScale by remember { mutableStateOf(1f) }

    // Blood group options (aligned with typical values)
    val bloodGroupOptions = listOf(
        "Select", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    )

    LaunchedEffect(state) {
        when (state) {
            is PatientProfileState.Success -> {
                onSave((state as PatientProfileState.Success).profile.name)
                toastController.show("Profile updated successfully")
            }
            is PatientProfileState.Error -> {
                toastController.show((state as PatientProfileState.Error).message)
            }
            else -> Unit
        }
    }

/*    LaunchedEffect(patientId) {
        viewModel.fetchPatientProfile()
    }*/

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Patient Profile",
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
                actions = {
                    TextButton(
                        onClick = { viewModel.toggleEditMode() },
                        enabled = state !is PatientProfileState.Loading
                    ) {
                        Text(
                            if (isEditing) "Cancel Edit" else "Edit",
                            color = ColorPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F5F5),
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InputField(
                        value = formState.patientId,
                        onValueChange = { /* Read-only */ },
                        label = "Patient ID",
                        leadingIcon = Icons.Default.Info,
                        errorMessage = formState.fieldErrors["patientId"],
                        contentDescription = "Patient ID icon",
                        readOnly = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    InputField(
                        value = formState.mobileNumber,
                        onValueChange = { /* Read-only */ },
                        label = "Mobile Number",
                        leadingIcon = Icons.Default.Phone,
                        errorMessage = formState.fieldErrors["mobileNumber"],
                        contentDescription = "Mobile number icon",
                        readOnly = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        )
                    )
                    InputField(
                        value = formState.fullName,
                        onValueChange = { viewModel.updateFormState(formState.copy(fullName = it)) },
                        label = "Full Name",
                        leadingIcon = Icons.Default.Person,
                        errorMessage = formState.fieldErrors["fullName"],
                        contentDescription = "Full name icon",
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    InputField(
                        value = formState.guardianName,
                        onValueChange = { viewModel.updateFormState(formState.copy(guardianName = it)) },
                        label = "Guardian Name",
                        leadingIcon = IconGuardian,
                        errorMessage = formState.fieldErrors["guardianName"],
                        contentDescription = "Guardian name icon",
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    ExposedDropdownMenuBox(
                        expanded = bloodExpanded && isEditing,
                        onExpandedChange = { if (isEditing) bloodExpanded = !bloodExpanded }
                    ) {
                        InputField(
                            value = formState.bloodGroup,
                            onValueChange = { /* read-only in dropdown */ },
                            label = "Blood Group",
                            leadingIcon = IconDrop,
                            errorMessage = formState.fieldErrors["bloodGroup"],
                            contentDescription = "Blood group icon",
                            readOnly = !isEditing,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded && isEditing)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = bloodExpanded && isEditing,
                            onDismissRequest = { bloodExpanded = false }
                        ) {
                            bloodGroupOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 16.sp) },
                                    onClick = {
                                        viewModel.updateFormState(formState.copy(bloodGroup = option))
                                        bloodExpanded = false
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Gender.entries.forEach { gender ->
                            FilterChip(
                                selected = formState.gender == gender,
                                onClick = {
                                    if (isEditing) viewModel.updateFormState(formState.copy(gender = gender))
                                },
                                label = { Text(gender.label, fontSize = 14.sp) },
                                leadingIcon = {
                                    when (gender) {
                                        Gender.Male -> IconMale
                                        Gender.Female -> IconFemale
                                        Gender.Other -> IconOther
                                    }
                                },
                                enabled = isEditing,
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ColorPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = ColorPrimary,
                                    selectedLeadingIconColor = ColorPrimary
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Relation.entries.forEach { relation ->
                            FilterChip(
                                selected = formState.relation == relation,
                                onClick = {
                                    if (isEditing) viewModel.updateFormState(formState.copy(relation = relation))
                                },
                                label = { Text(relation.label, fontSize = 14.sp) },
                                enabled = isEditing,
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ColorPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = ColorPrimary
                                )
                            )
                        }
                    }
                    InputField(
                        value = formState.dob,
                        onValueChange = { viewModel.updateFormState(formState.copy(dob = it)) },
                        label = "Age",
                        leadingIcon = Icons.Default.DateRange,
                        errorMessage = formState.fieldErrors["dob"],
                        contentDescription = "Age icon",
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                    InputField(
                        value = formState.email,
                        onValueChange = { viewModel.updateFormState(formState.copy(email = it)) },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        errorMessage = formState.fieldErrors["email"],
                        contentDescription = "Email icon",
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    InputField(
                        value = formState.address,
                        onValueChange = { viewModel.updateFormState(formState.copy(address = it)) },
                        label = "Address",
                        leadingIcon = Icons.Default.Home,
                        errorMessage = formState.fieldErrors["address"],
                        contentDescription = "Address icon",
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    InputField(
                        value = formState.city,
                        onValueChange = { viewModel.updateFormState(formState.copy(city = it)) },
                        label = "City",
                        leadingIcon = IconCity,
                        errorMessage = formState.fieldErrors["city"],
                        contentDescription = "City icon",
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    InputField(
                        value = formState.state,
                        onValueChange = { viewModel.updateFormState(formState.copy(state = it)) },
                        label = "State",
                        leadingIcon = IconMap,
                        errorMessage = formState.fieldErrors["state"],
                        contentDescription = "State icon",
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    AnimatedVisibility(visible = isEditing) {
                        ElevatedButton(
                            onClick = {
                                updateButtonScale = 0.95f
                                viewModel.updatePatientProfile()
                                updateButtonScale = 1f
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorPrimary,
                                contentColor = White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .scale(updateButtonScale)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = state !is PatientProfileState.Loading,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            if (state is PatientProfileState.Loading) {
                                CircularProgressIndicator(
                                    color = White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Update profile icon",
                                    tint = White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Update",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                AnimatedVisibility(
                    visible = state is PatientProfileState.Loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable(enabled = false) { },
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ColorPrimary,
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp
                        )
                    }
                }
            }
        }
    )
}
