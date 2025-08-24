package com.rotary.hospital.feature.patient.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.component.InputField
import com.rotary.hospital.core.ui.toastController
import com.rotary.hospital.feature.patient.presentation.viewmodel.*
import org.koin.compose.viewmodel.koinViewModel
import appicon.IconDrop
import appicon.IconFemale
import appicon.IconGuardian
import appicon.IconMale
import appicon.IconOther
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.appicon.IconCity
import com.rotary.hospital.core.common.appicon.IconMap
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun PatientProfileScreen(
    onBack: () -> Unit,
    onSave: (String) -> Unit,
    viewModel: PatientProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    var bloodExpanded by remember { mutableStateOf(false) }

    // for buttons ui
    var saveButtonScale by remember { mutableStateOf(1f) }
    var cancelButtonScale by remember { mutableStateOf(1f) }

    val notProvided = "Not provided"

    LaunchedEffect(state) {
        when (state) {
            is PatientProfileState.UpdateSuccess -> {
                onSave((state as PatientProfileState.UpdateSuccess).profile.name)
                toastController.show("Profile updated successfully")
            }

            is PatientProfileState.Error -> {
                Logger.e("PatientProfileScreen", (state as PatientProfileState.Error).message)
                toastController.show((state as PatientProfileState.Error).message)
            }

            else -> Unit
        }
    }

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
                            tint = ColorPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Color.Black
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isEditing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { viewModel.toggleEditMode() },
                    containerColor = ColorPrimary,
                    contentColor = White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 2.dp
                    )
                    .padding(bottom = if (isEditing) 80.dp else 0.dp), // Add padding to avoid overlap with buttons
                verticalArrangement = Arrangement.spacedBy(24.dp)
            )
            {
                item {
                    Spacer(Modifier.height(2.dp))
                }

                // -------------------------
                // Personal Info Card
                // -------------------------
                item {
                    SectionCard(title = "Personal Info") {
                        // Always read-only fields
                        StaticField(
                            label = "Patient ID",
                            value = formState.patientId,
                            leadingIcon = Icons.Default.Info,
                            notProvided = notProvided
                        )
                        Spacer(Modifier.height(12.dp))
                        StaticField(
                            label = "Mobile Number",
                            value = formState.mobileNumber,
                            leadingIcon = Icons.Default.Info,
                            notProvided = notProvided
                        )
                        Spacer(Modifier.height(12.dp))

                        // CHANGE: Editable ONLY when isEditing; otherwise show StaticField (no “fake editable”)
                        if (isEditing) {
                            InputField(
                                value = formState.fullName,
                                onValueChange = { viewModel.updateFormState(formState.copy(fullName = it)) },
                                label = "Full Name",
                                leadingIcon = Icons.Default.Person,
                                errorMessage = formState.fieldErrors["fullName"],
                                contentDescription = "Full name icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        } else {
                            StaticField(
                                label = "Full Name",
                                value = formState.fullName,
                                leadingIcon = Icons.Default.Person,
                                notProvided = notProvided
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        // Gender chips — disabled when not editing
                        Text(
                            "Select Gender",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Gender.entries.forEach { g ->
                                FilterChip(
                                    selected = formState.gender == g,
                                    onClick = {
                                        if (isEditing) viewModel.updateFormState(
                                            formState.copy(
                                                gender = g
                                            )
                                        )
                                    },
                                    label = { Text(g.label, fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (g) {
                                                Gender.Male -> IconMale
                                                Gender.Female -> IconFemale
                                                else -> IconOther
                                            },
                                            contentDescription = "Gender ${g.label} icon",
                                            tint = if (formState.gender == g) ColorPrimary.copy(
                                                alpha = 0.8f
                                            ) else Color.Gray
                                        )
                                    },
                                    enabled = isEditing,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ColorPrimary.copy(alpha = 0.1f),
                                        selectedLabelColor = ColorPrimary,
                                        selectedLeadingIconColor = ColorPrimary.copy(alpha = 0.8f)
                                    ),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))

                        // DOB / Age (you named it dob in form; label can be Age or Date of Birth)
                        var showDatePicker by remember { mutableStateOf(false) }
                        val currentYear = kotlin.time.Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()).year
                        val datePickerState = rememberDatePickerState(
                            yearRange = 1900..currentYear,
                            selectableDates = object : SelectableDates {
                                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                    return utcTimeMillis <= kotlin.time.Clock.System.now()
                                        .toEpochMilliseconds()
                                }
                            }
                        )
                        if (isEditing) {
                            InputField(
                                value = formState.dob,
                                onValueChange = {},
                                readOnly = true,
                                label = "Select age by Date of Birth",
                                placeholder = "dd-mm-yyyy",
                                leadingIcon = Icons.Default.DateRange,
                                errorMessage = formState.fieldErrors["dob"],
                                contentDescription = "Date of birth icon",
                                modifier = Modifier
                                    .fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                onClick = {
                                    showDatePicker = true
                                }
                            )
                        } else {
                            StaticField(
                                label = "Age",
                                value = formState.dob,
                                leadingIcon = Icons.Default.DateRange, // or Icons.Default.DateRange
                                notProvided = notProvided
                            )
                        }
                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDatePicker = false
                                            datePickerState.selectedDateMillis?.let { millis ->
                                                val selectedDate =
                                                    kotlin.time.Instant.fromEpochMilliseconds(
                                                        millis
                                                    )
                                                        .toLocalDateTime(TimeZone.UTC)
                                                        .date
                                                val formattedDate = "${
                                                    selectedDate.day.toString().padStart(2, '0')
                                                }-" +
                                                        "${
                                                            selectedDate.month.number.toString()
                                                                .padStart(2, '0')
                                                        }-" +
                                                        "${selectedDate.year}"
                                                val age = calculateAge(formattedDate)
                                                age?.let {
                                                    viewModel.updateFormState(
                                                        formState.copy(
                                                            dob = it
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Confirm")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("Cancel")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }


                        Spacer(Modifier.height(12.dp))

                        // Blood Group dropdown when editing, static when not
                        if (isEditing) {
                            val groups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
                            ExposedDropdownMenuBox(
                                expanded = bloodExpanded,
                                onExpandedChange = { bloodExpanded = !bloodExpanded }
                            ) {
                                InputField(
                                    value = formState.bloodGroup,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = "Blood Group",
                                    leadingIcon = IconDrop,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded)
                                    },
                                    errorMessage = formState.fieldErrors["bloodGroup"],
                                    contentDescription = "Blood group icon",
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = bloodExpanded,
                                    onDismissRequest = { bloodExpanded = false },
                                    modifier = Modifier.background(White)
                                ) {
                                    groups.forEach { group ->
                                        DropdownMenuItem(
                                            text = { Text(group, fontSize = 16.sp) },
                                            onClick = {
                                                viewModel.updateFormState(formState.copy(bloodGroup = group))
                                                bloodExpanded = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        } else {
                            StaticField(
                                label = "Blood Group",
                                value = formState.bloodGroup,
                                leadingIcon = IconDrop,
                                notProvided = notProvided
                            )
                        }
                    }
                }
                // -------------------------
                // Guardian Info Card
                // -------------------------
                item {
                    SectionCard(title = "Guardian Info") {
                        if (isEditing) {
                            InputField(
                                value = formState.guardianName,
                                onValueChange = {
                                    viewModel.updateFormState(
                                        formState.copy(
                                            guardianName = it
                                        )
                                    )
                                },
                                label = "Guardian Name",
                                leadingIcon = IconGuardian,
                                errorMessage = formState.fieldErrors["guardianName"],
                                contentDescription = "Guardian name icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        } else {
                            StaticField(
                                label = "Guardian Name",
                                value = formState.guardianName,
                                leadingIcon = IconGuardian,
                                notProvided = notProvided
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Relation to Guardian",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Relation.entries.forEach { rel ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.updateFormState(formState.copy(relation = rel))
                                        }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    RadioButton(
                                        selected = formState.relation == rel,
                                        onClick = {
                                            viewModel.updateFormState(formState.copy(relation = rel))
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = ColorPrimary.copy(alpha = 0.8f),
                                            unselectedColor = Color.Gray
                                        )
                                    )
                                    Text(
                                        rel.label,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                // -------------------------
                // Contact Info Card
                // -------------------------
                item {
                    SectionCard(title = "Contact Info") {
                        if (isEditing) {
                            InputField(
                                value = formState.email,
                                onValueChange = { viewModel.updateFormState(formState.copy(email = it)) },
                                label = "Email",
                                leadingIcon = Icons.Default.Email,
                                errorMessage = formState.fieldErrors["email"],
                                contentDescription = "Email icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                )
                            )
                        } else {
                            StaticField(
                                label = "Email",
                                value = formState.email,
                                leadingIcon = Icons.Default.Email,
                                notProvided = notProvided
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        if (isEditing) {
                            InputField(
                                value = formState.address,
                                onValueChange = { viewModel.updateFormState(formState.copy(address = it)) },
                                label = "Address",
                                leadingIcon = Icons.Default.Home,
                                errorMessage = formState.fieldErrors["address"],
                                contentDescription = "Address icon",
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        } else {
                            StaticField(
                                label = "Address",
                                value = formState.address,
                                leadingIcon = Icons.Default.Home,
                                notProvided = notProvided
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (isEditing) {
                                InputField(
                                    value = formState.city,
                                    onValueChange = { viewModel.updateFormState(formState.copy(city = it)) },
                                    label = "City",
                                    leadingIcon = IconCity,
                                    errorMessage = formState.fieldErrors["city"],
                                    contentDescription = "City icon",
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                                )
                                InputField(
                                    value = formState.state,
                                    onValueChange = { viewModel.updateFormState(formState.copy(state = it)) },
                                    label = "State",
                                    leadingIcon = IconMap,
                                    errorMessage = formState.fieldErrors["state"],
                                    contentDescription = "State icon",
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                                )
                            } else {
                                StaticField(
                                    label = "City",
                                    value = formState.city,
                                    leadingIcon = IconCity,
                                    notProvided = notProvided,
                                    modifier = Modifier.weight(1f)
                                )
                                StaticField(
                                    label = "State",
                                    value = formState.state,
                                    leadingIcon = IconMap,
                                    notProvided = notProvided,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                // -------------------------
                // Errors (inline)
                // -------------------------
                item {
                    AnimatedVisibility(
                        visible = state is PatientProfileState.Error,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (state is PatientProfileState.Error) {
                            Text(
                                text = (state as PatientProfileState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                }
            }

            // -------------------------
            // Overlay for Update/Cancel buttons
            // -------------------------
            // Bottom fixed buttons for Cancel and Update when editing
            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            cancelButtonScale = 0.95f
                            viewModel.toggleEditMode()
                            cancelButtonScale = 1f
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .scale(cancelButtonScale)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = state !is PatientProfileState.Loading,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Cancel edit",
                            tint = White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    ElevatedButton(
                        onClick = {
                            saveButtonScale = 0.95f
                            viewModel.updatePatientProfile()
                            saveButtonScale = 1f
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrimary,
                            contentColor = White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .scale(saveButtonScale)
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
            }

            // -------------------------
            // Loading overlay for progress bar icon
            // -------------------------
            AnimatedVisibility(
                visible = state is PatientProfileState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f))
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
}

/**
 * CHANGE: Section wrapper to match RegistrationScreen card look
 */
@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(White)
                .padding(20.dp)
        ) {
            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

/**
 * CHANGE: Read-only field (no “fake editable” text field)
 * - Shows label + value in a soft container
 * - Uses “Not provided” when value is blank
 */
@Composable
private fun StaticField(
    label: String,
    value: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    notProvided: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ColorPrimary.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = value.ifBlank { notProvided },
                style = MaterialTheme.typography.bodyMedium,
                color = if (value.isBlank())
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
