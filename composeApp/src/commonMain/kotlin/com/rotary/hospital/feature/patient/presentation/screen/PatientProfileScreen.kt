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
import org.jetbrains.compose.resources.stringResource
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.address
import rotaryhospital.composeapp.generated.resources.age
import rotaryhospital.composeapp.generated.resources.back
import rotaryhospital.composeapp.generated.resources.blood_group
import rotaryhospital.composeapp.generated.resources.cancel
import rotaryhospital.composeapp.generated.resources.city
import rotaryhospital.composeapp.generated.resources.confirm
import rotaryhospital.composeapp.generated.resources.contact_info
import rotaryhospital.composeapp.generated.resources.edit_profile
import rotaryhospital.composeapp.generated.resources.email
import rotaryhospital.composeapp.generated.resources.full_name
import rotaryhospital.composeapp.generated.resources.gender_female
import rotaryhospital.composeapp.generated.resources.gender_icon_description
import rotaryhospital.composeapp.generated.resources.gender_male
import rotaryhospital.composeapp.generated.resources.gender_other
import rotaryhospital.composeapp.generated.resources.guardian_info
import rotaryhospital.composeapp.generated.resources.guardian_name
import rotaryhospital.composeapp.generated.resources.mobile_number
import rotaryhospital.composeapp.generated.resources.not_provided
import rotaryhospital.composeapp.generated.resources.patient_id
import rotaryhospital.composeapp.generated.resources.patient_profile
import rotaryhospital.composeapp.generated.resources.personal_info
import rotaryhospital.composeapp.generated.resources.relation_daughter_of
import rotaryhospital.composeapp.generated.resources.relation_guardian
import rotaryhospital.composeapp.generated.resources.relation_son_of
import rotaryhospital.composeapp.generated.resources.relation_wife_of
import rotaryhospital.composeapp.generated.resources.select_dob
import rotaryhospital.composeapp.generated.resources.select_gender
import rotaryhospital.composeapp.generated.resources.state
import rotaryhospital.composeapp.generated.resources.update
import kotlin.time.ExperimentalTime
import com.rotary.hospital.core.domain.UiText


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

    val notProvided = stringResource(Res.string.not_provided)

    // This LaunchedEffect will show toasts for success or error states
    val currentState = state
    if (currentState is PatientProfileState.Error || currentState is PatientProfileState.UpdateSuccess) {
        val message = when (currentState) {
            is PatientProfileState.UpdateSuccess -> "Profile updated successfully"
            is PatientProfileState.Error -> currentState.message.asString()
            else -> null
        }
        LaunchedEffect(message) {
            if (message != null) {
                toastController.show(message)
                if (currentState is PatientProfileState.UpdateSuccess) {
                    onSave(currentState.profile.name)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.patient_profile),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ColorPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
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
                        contentDescription = stringResource(Res.string.edit_profile),
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
                    SectionCard(title = stringResource(Res.string.personal_info)) {
                        // Always read-only fields
                        StaticField(
                            label = stringResource(Res.string.patient_id),
                            value = formState.patientId,
                            leadingIcon = Icons.Default.Info,
                            notProvided = notProvided
                        )
                        Spacer(Modifier.height(12.dp))
                        StaticField(
                            label = stringResource(Res.string.mobile_number),
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
                                label = stringResource(Res.string.full_name),
                                leadingIcon = Icons.Default.Person,
                                errorMessage = formState.fieldErrors["fullName"],
                                contentDescription = stringResource(Res.string.full_name),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        } else {
                            StaticField(
                                label = stringResource(Res.string.full_name),
                                value = formState.fullName,
                                leadingIcon = Icons.Default.Person,
                                notProvided = notProvided
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        // Gender chips — disabled when not editing
                        Text(
                            stringResource(Res.string.select_gender),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Gender.entries.forEach { gender ->
                                FilterChip(
                                    selected = formState.gender == gender,
                                    onClick = {
                                        if (isEditing) viewModel.updateFormState(
                                            formState.copy(
                                                gender = gender
                                            )
                                        )
                                    },
                                    label = {
                                        Text(
                                            getGenderLabel(gender), fontSize = 14.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (gender) {
                                                Gender.Male -> IconMale
                                                Gender.Female -> IconFemale
                                                else -> IconOther
                                            },
                                            contentDescription = stringResource(
                                                Res.string.gender_icon_description,
                                                getGenderLabel(gender)
                                            ),
                                            tint = if (formState.gender == gender) ColorPrimary.copy(
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
                                label = stringResource(Res.string.select_dob),
                                placeholder = "dd-mm-yyyy",
                                leadingIcon = Icons.Default.DateRange,
                                errorMessage = formState.fieldErrors["dob"],
                                contentDescription = stringResource(Res.string.select_dob),
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
                                label = stringResource(Res.string.age),
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
                                        Text(stringResource(Res.string.confirm))
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text(stringResource(Res.string.cancel))
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
                                    label = stringResource(Res.string.blood_group),
                                    leadingIcon = IconDrop,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded)
                                    },
                                    errorMessage = formState.fieldErrors["bloodGroup"],
                                    contentDescription = stringResource(Res.string.blood_group),
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
                                label = stringResource(Res.string.blood_group),
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
                    SectionCard(title = stringResource(Res.string.guardian_info)) {
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
                                label = stringResource(Res.string.guardian_name),
                                leadingIcon = IconGuardian,
                                errorMessage = formState.fieldErrors["guardianName"],
                                contentDescription = stringResource(Res.string.guardian_name),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        } else {
                            StaticField(
                                label = stringResource(Res.string.guardian_name),
                                value = formState.guardianName,
                                leadingIcon = IconGuardian,
                                notProvided = notProvided
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        Text(
                            stringResource(Res.string.relation_guardian),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Relation.entries.forEach { relation ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.updateFormState(formState.copy(relation = relation))
                                        }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    RadioButton(
                                        selected = formState.relation == relation,
                                        onClick = {
                                            viewModel.updateFormState(formState.copy(relation = relation))
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = ColorPrimary.copy(alpha = 0.8f),
                                            unselectedColor = Color.Gray
                                        )
                                    )
                                    Text(
                                        getRelationLabel(relation),
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
                    SectionCard(title = stringResource(Res.string.contact_info)) {
                        if (isEditing) {
                            InputField(
                                value = formState.email,
                                onValueChange = { viewModel.updateFormState(formState.copy(email = it)) },
                                label = stringResource(Res.string.email),
                                leadingIcon = Icons.Default.Email,
                                errorMessage = formState.fieldErrors["email"],
                                contentDescription = stringResource(Res.string.email),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                )
                            )
                        } else {
                            StaticField(
                                label = stringResource(Res.string.email),
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
                                label = stringResource(Res.string.address),
                                leadingIcon = Icons.Default.Home,
                                errorMessage = formState.fieldErrors["address"],
                                contentDescription = stringResource(Res.string.address),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        } else {
                            StaticField(
                                label = stringResource(Res.string.address),
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
                                    label = stringResource(Res.string.city),
                                    leadingIcon = IconCity,
                                    errorMessage = formState.fieldErrors["city"],
                                    contentDescription = stringResource(Res.string.city),
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                                )
                                InputField(
                                    value = formState.state,
                                    onValueChange = { viewModel.updateFormState(formState.copy(state = it)) },
                                    label = stringResource(Res.string.state),
                                    leadingIcon = IconMap,
                                    errorMessage = formState.fieldErrors["state"],
                                    contentDescription = stringResource(Res.string.state),
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                                )
                            } else {
                                StaticField(
                                    label = stringResource(Res.string.city),
                                    value = formState.city,
                                    leadingIcon = IconCity,
                                    notProvided = notProvided,
                                    modifier = Modifier.weight(1f)
                                )
                                StaticField(
                                    label = stringResource(Res.string.state),
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
                        visible = state is PatientProfileState.Error && formState.fieldErrors.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (state is PatientProfileState.Error) {
                            Text(
                                text = (state as PatientProfileState.Error).message.asString(),
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
                            contentDescription = stringResource(Res.string.cancel),
                            tint = White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(Res.string.cancel),
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
                                contentDescription = stringResource(Res.string.update),
                                tint = White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(Res.string.update),
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

@Composable
private fun getGenderLabel(gender: Gender) = when (gender) {
    Gender.Male -> stringResource(Res.string.gender_male)
    Gender.Female -> stringResource(Res.string.gender_female)
    Gender.Other -> stringResource(Res.string.gender_other)
}

@Composable
private fun getRelationLabel(relation: Relation) = when (relation) {
    Relation.SonOf -> stringResource(Res.string.relation_son_of)
    Relation.DaughterOf -> stringResource(Res.string.relation_daughter_of)
    Relation.WifeOf -> stringResource(Res.string.relation_wife_of)
}

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
