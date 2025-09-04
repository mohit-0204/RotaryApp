@file:OptIn(ExperimentalTime::class)

package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import appicon.IconDrop
import appicon.IconFemale
import appicon.IconGuardian
import appicon.IconMale
import appicon.IconOther
import com.rotary.hospital.core.common.appicon.IconCity
import com.rotary.hospital.core.common.appicon.IconMap
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.component.InputField
import com.rotary.hospital.feature.opd.presentation.viewmodel.Gender
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPatientRegistrationState
import com.rotary.hospital.feature.opd.presentation.viewmodel.OpdPatientRegistrationViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.Relation
import com.rotary.hospital.feature.patient.presentation.viewmodel.calculateAge
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.address
import rotaryhospital.composeapp.generated.resources.back
import rotaryhospital.composeapp.generated.resources.blood_group
import rotaryhospital.composeapp.generated.resources.cancel
import rotaryhospital.composeapp.generated.resources.city
import rotaryhospital.composeapp.generated.resources.confirm
import rotaryhospital.composeapp.generated.resources.contact_info
import rotaryhospital.composeapp.generated.resources.email
import rotaryhospital.composeapp.generated.resources.full_name
import rotaryhospital.composeapp.generated.resources.gender_female
import rotaryhospital.composeapp.generated.resources.gender_icon_description
import rotaryhospital.composeapp.generated.resources.gender_male
import rotaryhospital.composeapp.generated.resources.gender_other
import rotaryhospital.composeapp.generated.resources.guardian_info
import rotaryhospital.composeapp.generated.resources.guardian_name
import rotaryhospital.composeapp.generated.resources.opd_patient_registration_title
import rotaryhospital.composeapp.generated.resources.personal_info
import rotaryhospital.composeapp.generated.resources.relation_daughter_of
import rotaryhospital.composeapp.generated.resources.relation_guardian
import rotaryhospital.composeapp.generated.resources.relation_son_of
import rotaryhospital.composeapp.generated.resources.relation_wife_of
import rotaryhospital.composeapp.generated.resources.save
import rotaryhospital.composeapp.generated.resources.select_dob
import rotaryhospital.composeapp.generated.resources.select_gender
import rotaryhospital.composeapp.generated.resources.state
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpdPatientRegistrationScreen(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onSave: (String, String) -> Unit,
    viewModel: OpdPatientRegistrationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val formState by viewModel.formState.collectAsState()
    var bloodExpanded by remember { mutableStateOf(false) }
    var cancelButtonScale by remember { mutableStateOf(1f) }
    var saveButtonScale by remember { mutableStateOf(1f) }

    LaunchedEffect(state) {
        if (state is OpdPatientRegistrationState.Success) {
            val patient = (state as OpdPatientRegistrationState.Success).patient
            onSave(patient.id, patient.name)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.opd_patient_registration_title),
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
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEEEEEE))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        cancelButtonScale = 0.95f
                        onCancel()
                        cancelButtonScale = 1f
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .scale(cancelButtonScale),
                    shape = RoundedCornerShape(14.dp),
                    enabled = state !is OpdPatientRegistrationState.Loading,
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
                        viewModel.registerPatient()
                        saveButtonScale = 1f
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimary,
                        contentColor = White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .scale(saveButtonScale),
                    shape = RoundedCornerShape(12.dp),
                    enabled = state !is OpdPatientRegistrationState.Loading,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    if (state is OpdPatientRegistrationState.Loading) {
                        CircularProgressIndicator(
                            color = White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(Res.string.save),
                            tint = White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(Res.string.save),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Spacer(Modifier.height(16.dp))
                    // Personal Info Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.background(White).padding(20.dp)
                        ) {
                            Text(
                                stringResource(Res.string.personal_info),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorPrimary
                            )
                            Spacer(Modifier.height(16.dp))

                            InputField(
                                value = formState.fullName,
                                onValueChange = {
                                    viewModel.updateFormState(formState.copy(fullName = it))
                                },
                                label = stringResource(Res.string.full_name),
                                leadingIcon = Icons.Default.Person,
                                errorMessage = formState.fieldErrors["fullName"],
                                contentDescription = stringResource(Res.string.full_name),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                            Spacer(Modifier.height(12.dp))

                            Text(
                                stringResource(Res.string.select_gender),
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Gender.entries.forEach { gender ->
                                    FilterChip(
                                        selected = formState.gender == gender,
                                        onClick = {
                                            viewModel.updateFormState(formState.copy(gender = gender))
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
                                                    Gender.Other -> IconOther
                                                },
                                                contentDescription = stringResource(
                                                    Res.string.gender_icon_description,
                                                    getGenderLabel(gender)
                                                ),
                                                tint = if (formState.gender == gender) ColorPrimary else Color.Gray
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ColorPrimary.copy(alpha = 0.1f),
                                            selectedLabelColor = ColorPrimary,
                                            selectedLeadingIconColor = ColorPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                            // DOB Field with Date Picker
                            var showDatePicker by remember { mutableStateOf(false) }
                            val currentYear = kotlin.time.Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault()).year
                            val datePickerState = rememberDatePickerState(
                                yearRange = 1900..currentYear,
                                selectableDates = object : SelectableDates {
                                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                        return utcTimeMillis <= Clock.System.now()
                                            .toEpochMilliseconds()
                                    }
                                }
                            )

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

                            ExposedDropdownMenuBox(
                                expanded = bloodExpanded,
                                onExpandedChange = { bloodExpanded = !bloodExpanded },
                                modifier = Modifier.fillMaxWidth()
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
                                    listOf(
                                        "A+",
                                        "A-",
                                        "B+",
                                        "B-",
                                        "O+",
                                        "O-",
                                        "AB+",
                                        "AB-"
                                    ).forEach { group ->
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
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Guardian Info Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.background(White).padding(20.dp)
                        ) {
                            Text(
                                stringResource(Res.string.guardian_info),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorPrimary
                            )
                            Spacer(Modifier.height(16.dp))

                            InputField(
                                value = formState.guardianName,
                                onValueChange = {
                                    viewModel.updateFormState(formState.copy(guardianName = it))
                                },
                                label = stringResource(Res.string.guardian_name),
                                leadingIcon = IconGuardian,
                                errorMessage = formState.fieldErrors["guardianName"],
                                contentDescription = stringResource(Res.string.guardian_name),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
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
                                            getRelationLabel(rel),
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Contact Info Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.background(White).padding(20.dp)
                        ) {
                            Text(
                                stringResource(Res.string.contact_info),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorPrimary
                            )
                            Spacer(Modifier.height(16.dp))

                            InputField(
                                value = formState.email,
                                onValueChange = {
                                    viewModel.updateFormState(formState.copy(email = it))
                                },
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
                            Spacer(Modifier.height(12.dp))

                            InputField(
                                value = formState.address,
                                onValueChange = {
                                    viewModel.updateFormState(formState.copy(address = it))
                                },
                                label = stringResource(Res.string.address),
                                leadingIcon = Icons.Default.Home,
                                errorMessage = formState.fieldErrors["address"],
                                contentDescription = stringResource(Res.string.address),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                InputField(
                                    value = formState.city,
                                    onValueChange = {
                                        viewModel.updateFormState(formState.copy(city = it))
                                    },
                                    label = stringResource(Res.string.city),
                                    leadingIcon = IconCity,
                                    errorMessage = formState.fieldErrors["city"],
                                    contentDescription = stringResource(Res.string.city),
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    )
                                )
                                InputField(
                                    value = formState.state,
                                    onValueChange = {
                                        viewModel.updateFormState(formState.copy(state = it))
                                    },
                                    label = stringResource(Res.string.state),
                                    leadingIcon = IconMap,
                                    errorMessage = formState.fieldErrors["state"],
                                    contentDescription = stringResource(Res.string.state),
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = state is OpdPatientRegistrationState.Error,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (state is OpdPatientRegistrationState.Error) {
                            Text(
                                text = (state as OpdPatientRegistrationState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }


                // -------------------------
                // Loading overlay for progress bar icon
                // -------------------------
                AnimatedVisibility(
                    visible = state is OpdPatientRegistrationState.Loading,
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
    )
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