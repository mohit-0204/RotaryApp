@file:OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.patient.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
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
import com.rotary.hospital.core.domain.UiText
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.Surface
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.component.InputField
import com.rotary.hospital.feature.patient.presentation.viewmodel.Gender
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientRegistrationState
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientRegistrationViewModel
import com.rotary.hospital.feature.patient.presentation.viewmodel.RegistrationEvent
import com.rotary.hospital.feature.patient.presentation.viewmodel.RegistrationFormState
import com.rotary.hospital.feature.patient.presentation.viewmodel.Relation
import com.rotary.hospital.feature.patient.presentation.viewmodel.calculateAge
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
import rotaryhospital.composeapp.generated.resources.choose_guardian
import rotaryhospital.composeapp.generated.resources.city
import rotaryhospital.composeapp.generated.resources.confirm
import rotaryhospital.composeapp.generated.resources.contact_info
import rotaryhospital.composeapp.generated.resources.email
import rotaryhospital.composeapp.generated.resources.error
import rotaryhospital.composeapp.generated.resources.full_name
import rotaryhospital.composeapp.generated.resources.gender_female
import rotaryhospital.composeapp.generated.resources.gender_icon_description
import rotaryhospital.composeapp.generated.resources.gender_male
import rotaryhospital.composeapp.generated.resources.gender_other
import rotaryhospital.composeapp.generated.resources.guardian_info
import rotaryhospital.composeapp.generated.resources.guardian_name
import rotaryhospital.composeapp.generated.resources.personal_info
import rotaryhospital.composeapp.generated.resources.register_new_patient
import rotaryhospital.composeapp.generated.resources.relation_daughter_of
import rotaryhospital.composeapp.generated.resources.relation_son_of
import rotaryhospital.composeapp.generated.resources.relation_wife_of
import rotaryhospital.composeapp.generated.resources.save
import rotaryhospital.composeapp.generated.resources.select_dob
import rotaryhospital.composeapp.generated.resources.select_gender
import rotaryhospital.composeapp.generated.resources.state
import kotlin.time.ExperimentalTime

@Composable
fun PatientRegistrationScreen(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onSave: (String) -> Unit,
    viewModel: PatientRegistrationViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
) {
    val state by viewModel.state.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // We now store the UiText object for the dialog, not the resolved String.
    var dialogMessageUiText by remember { mutableStateOf<UiText?>(null) }

    // State to hold the UiText for the snackbar message.
    var snackbarMessageUiText by remember { mutableStateOf<UiText?>(null) }

    // These interaction sources are used to create a press-and-release animation on the buttons.
    val cancelInteractionSource = remember { MutableInteractionSource() }
    val isCancelPressed by cancelInteractionSource.collectIsPressedAsState()
    val cancelButtonScale by animateFloatAsState(targetValue = if (isCancelPressed) 0.95f else 1f)

    val saveInteractionSource = remember { MutableInteractionSource() }
    val isSavePressed by saveInteractionSource.collectIsPressedAsState()
    val saveButtonScale by animateFloatAsState(targetValue = if (isSavePressed) 0.95f else 1f)

    // This block handles the automatic scrolling to the first validation error.
    val scrollState = rememberScrollState()
    val fieldPositions = remember { mutableStateMapOf<String, Float>() }

    LaunchedEffect(formState.firstErrorField) {
        // When the ViewModel sets the firstErrorField, this effect will trigger.
        formState.firstErrorField?.let { key ->
            coroutineScope.launch {
                // Find the Y position of the card containing the error field.
                val yPosition = fieldPositions[key]
                if (yPosition != null) {
                    // Animate the scroll to that position.
                    scrollState.animateScrollTo(yPosition.toInt())
                }
                // Reset the trigger in the ViewModel so it doesn't re-trigger on recomposition.
                viewModel.clearScrollToError()
            }
        }
    }

    // This LaunchedEffect collects events from the ViewModel.
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is RegistrationEvent.NavigateOnSuccess -> {
                    onSave(event.patientName)
                }
                is RegistrationEvent.ShowSnackbar -> {
                    // Instead of showing the snackbar directly, we set the state.
                    snackbarMessageUiText = event.message
                }
                is RegistrationEvent.ShowDialog -> {
                    // Set the UiText object to be shown in the dialog.
                    dialogMessageUiText = event.message
                }
            }
        }
    }

    // It runs when snackbarMessageUiText changes & handle showing the snackbar.
    snackbarMessageUiText?.let { message ->
        // We resolve the string here, in the main body of the Composable, which is allowed.
        val snackbarText = message.asString()
        LaunchedEffect(snackbarText) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message = snackbarText)
                // Reset the state so the same message can be shown again if needed.
                snackbarMessageUiText = null
            }
        }
    }

    // This composable will render the AlertDialog when dialogMessageUiText is not null
    if (dialogMessageUiText != null) {
        AlertDialog(
            onDismissRequest = { dialogMessageUiText = null },
            title = { Text(text = stringResource(Res.string.error)) },
            // We call .asString() here, inside a Composable context, which is valid.
            text = { Text(text = dialogMessageUiText!!.asString()) },
            confirmButton = {
                TextButton(onClick = { dialogMessageUiText = null }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.register_new_patient),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ColorPrimary
                    )
                }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                            tint = ColorPrimary
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White, titleContentColor = Color.Black
                )
            )
        }, bottomBar = {
            // -------------------------
            // Bottom fixed buttons for Cancel and Save
            // -------------------------
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFEEEEEE))
                    .padding(horizontal = 16.dp, vertical = 8.dp).navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error, contentColor = White
                    ),
                    modifier = Modifier.weight(1f).height(56.dp).scale(cancelButtonScale),
                    shape = RoundedCornerShape(14.dp),
                    enabled = state !is PatientRegistrationState.Loading,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    interactionSource = cancelInteractionSource
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
                    onClick = viewModel::registerPatient,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimary, contentColor = White
                    ),
                    modifier = Modifier.weight(1f).height(56.dp).scale(saveButtonScale),
                    shape = RoundedCornerShape(12.dp),
                    enabled = state !is PatientRegistrationState.Loading,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    interactionSource = saveInteractionSource
                ) {
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
        }, content = { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        // Attach the scrollState to the Column to make it scrollable.
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Spacer(Modifier.height(16.dp))

                    PersonalInfoCard(
                        formState = formState, onFormChange = viewModel::updateFormState,
                        // We attach a modifier to capture the Y position of this card in the layout for scrolling.
                        // Map this card's position to "fullName".
                        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                            fieldPositions["fullName"] = layoutCoordinates.positionInParent().y
                            fieldPositions["dob"] = layoutCoordinates.positionInParent().y
                            fieldPositions["bloodGroup"] = layoutCoordinates.positionInParent().y
                        })

                    Spacer(Modifier.height(24.dp))

                    GuardianInfoCard(
                        formState = formState, onFormChange = viewModel::updateFormState,
                        // Map this card's position to "guardianName".
                        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                            fieldPositions["guardianName"] = layoutCoordinates.positionInParent().y
                        })

                    Spacer(Modifier.height(24.dp))

                    ContactInfoCard(
                        formState = formState, onFormChange = viewModel::updateFormState,
                        // Map this card's position to the first field within it, "email".
                        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                            fieldPositions["email"] = layoutCoordinates.positionInParent().y
                            fieldPositions["address"] = layoutCoordinates.positionInParent().y
                            fieldPositions["city"] = layoutCoordinates.positionInParent().y
                            fieldPositions["state"] = layoutCoordinates.positionInParent().y
                        })

                    Spacer(Modifier.height(24.dp))
                    // The old AnimatedVisibility for the error text has been removed from here.
                }


                // -------------------------
                // Loading overlay for progress bar icon
                // -------------------------
                AnimatedVisibility(
                    visible = state is PatientRegistrationState.Loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f))
                            .clickable(enabled = false) { }, contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ColorPrimary,
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp
                        )
                    }
                }
            }
        })
}

@Composable
private fun PersonalInfoCard(
    formState: RegistrationFormState,
    onFormChange: (RegistrationFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    var bloodExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth()
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
                onValueChange = { onFormChange(formState.copy(fullName = it)) },
                label = stringResource(Res.string.full_name),
                leadingIcon = Icons.Default.Person,
                errorMessage = formState.fieldErrors["fullName"]?.asString(),
                contentDescription = stringResource(Res.string.full_name),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
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
                        onClick = { onFormChange(formState.copy(gender = gender)) },
                        label = { Text(getGenderLabel(gender), fontSize = 14.sp) },
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
                                tint = if (formState.gender == gender) ColorPrimary.copy(alpha = 0.8f) else Color.Gray
                            )
                        },
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

            // DOB selector with DatePicker
            val currentYear =
                kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
            val datePickerState = rememberDatePickerState(
                yearRange = 1900..currentYear,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= kotlin.time.Clock.System.now().toEpochMilliseconds()
                    }
                })

            InputField(
                value = formState.dob,
                onValueChange = {},
                readOnly = true,
                label = stringResource(Res.string.select_dob),
                placeholder = "dd-mm-yyyy",
                leadingIcon = Icons.Default.DateRange,
                errorMessage = formState.fieldErrors["dob"]?.asString(),
                contentDescription = stringResource(Res.string.select_dob),
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDatePicker = true }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate =
                                    kotlin.time.Instant.fromEpochMilliseconds(millis)
                                        .toLocalDateTime(TimeZone.UTC).date
                                val formattedDate =
                                    "${selectedDate.day.toString().padStart(2, '0')}-" +
                                            "${
                                                selectedDate.month.number.toString()
                                                    .padStart(2, '0')
                                            }-" +
                                            "${selectedDate.year}"
                                val age = calculateAge(formattedDate)
                                age?.let {
                                    onFormChange(formState.copy(dob = it))
                                }
                            }
                        }) {
                            Text(stringResource(Res.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(Res.string.cancel))
                        }
                    },
                    colors = DatePickerDefaults.colors(
                        containerColor = Color.White,
                    )
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            containerColor = Color.White,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                            headlineContentColor = MaterialTheme.colorScheme.primary,
                            weekdayContentColor = Color.DarkGray,
                            subheadContentColor = Color.DarkGray,
                            yearContentColor = Color.Black,
                            selectedYearContentColor = Color.White,
                            selectedYearContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            dayContentColor = Color.Black,
                            selectedDayContentColor = Color.White,
                            selectedDayContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            todayContentColor = MaterialTheme.colorScheme.primary
                        )
                    )

                }
            }

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = bloodExpanded,
                onExpandedChange = { bloodExpanded = !bloodExpanded }) {
                InputField(
                    value = formState.bloodGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = stringResource(Res.string.blood_group),
                    leadingIcon = IconDrop,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded) },
                    errorMessage = formState.fieldErrors["bloodGroup"]?.asString(),
                    contentDescription = stringResource(Res.string.blood_group),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = bloodExpanded,
                    onDismissRequest = { bloodExpanded = false },
                    modifier = Modifier.background(White)
                ) {
                    listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group, fontSize = 16.sp) },
                            onClick = {
                                onFormChange(formState.copy(bloodGroup = group))
                                bloodExpanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/*@Composable
private fun GuardianInfoCard(
    formState: RegistrationFormState,
    onFormChange: (RegistrationFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth()
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
                onValueChange = { onFormChange(formState.copy(guardianName = it)) },
                label = stringResource(Res.string.guardian_name),
                leadingIcon = IconGuardian,
                errorMessage = formState.fieldErrors["guardianName"]?.asString(),
                contentDescription = stringResource(Res.string.guardian_name),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(12.dp))

            Text(
                stringResource(Res.string.relation_guardian),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black
            )
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Relation.entries.forEach { rel ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable {
                            onFormChange(formState.copy(relation = rel))
                        }.padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = formState.relation == rel,
                            onClick = { onFormChange(formState.copy(relation = rel)) },
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
}*/

@Composable
private fun GuardianInfoCard(
    formState: RegistrationFormState,
    onFormChange: (RegistrationFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth()
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

            Text(
                stringResource(Res.string.choose_guardian),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(12.dp))

            // Use the new GuardianSelector component
            GuardianSelector(
                selectedType = formState.guardianType,
                onTypeSelected = { newType ->
                    onFormChange(formState.copy(guardianType = newType))
                }
            )

            Spacer(Modifier.height(12.dp))

            // InputField with a dynamic label
            InputField(
                value = formState.guardianName,
                onValueChange = { onFormChange(formState.copy(guardianName = it)) },
                label = if (formState.guardianType == PatientRegistrationViewModel.GuardianType.Father) {
                    "Father's Name"
                } else {
                    "Husband's Name"
                },
                leadingIcon = IconGuardian,
                errorMessage = formState.fieldErrors["guardianName"]?.asString(),
                contentDescription = stringResource(Res.string.guardian_name),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
        }
    }
}

@Composable
private fun ContactInfoCard(
    formState: RegistrationFormState,
    onFormChange: (RegistrationFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth()
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
                onValueChange = { onFormChange(formState.copy(email = it)) },
                label = stringResource(Res.string.email),
                leadingIcon = Icons.Default.Email,
                errorMessage = formState.fieldErrors["email"]?.asString(),
                contentDescription = stringResource(Res.string.email),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(12.dp))

            InputField(
                value = formState.address,
                onValueChange = { onFormChange(formState.copy(address = it)) },
                label = stringResource(Res.string.address),
                leadingIcon = Icons.Default.Home,
                errorMessage = formState.fieldErrors["address"]?.asString(),
                contentDescription = stringResource(Res.string.address),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InputField(
                    value = formState.city,
                    onValueChange = { onFormChange(formState.copy(city = it)) },
                    label = stringResource(Res.string.city),
                    leadingIcon = IconCity,
                    errorMessage = formState.fieldErrors["city"]?.asString(),
                    contentDescription = stringResource(Res.string.city),
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                    )
                )
                InputField(
                    value = formState.state,
                    onValueChange = { onFormChange(formState.copy(state = it)) },
                    label = stringResource(Res.string.state),
                    leadingIcon = IconMap,
                    errorMessage = formState.fieldErrors["state"]?.asString(),
                    contentDescription = stringResource(Res.string.state),
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                    )
                )
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
fun GuardianSelector(
    selectedType: PatientRegistrationViewModel.GuardianType,
    onTypeSelected: (PatientRegistrationViewModel.GuardianType) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = PatientRegistrationViewModel.GuardianType.entries

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.8f)),
        modifier = modifier.fillMaxWidth().height(52.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = selectedType == option

                // Determine the shape for rounded corners only on the ends
                val shape = when (index) {
                    0 -> RoundedCornerShape(topStart = 11.dp, bottomStart = 11.dp)
                    options.lastIndex -> RoundedCornerShape(topEnd = 11.dp, bottomEnd = 11.dp)
                    else -> RectangleShape
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = if (isSelected) ColorPrimary.copy(0.1f) else Color.Transparent,
                            shape = shape
                        )
                        .then(
                            if (isSelected) Modifier.border(
                                width = 1.5.dp,
                                color = ColorPrimary.copy(alpha = 0.7f),
                                shape = shape
                            ) else Modifier
                        )
                        .clickable { onTypeSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.name, // Displays "Father" or "Husband"
                        color = if (isSelected) ColorPrimary else Color.DarkGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                // Add a vertical divider between the chips
                if (index < options.lastIndex) {
                    Divider(
                        modifier = Modifier.fillMaxHeight(0.6f).width(1.dp),
                        color = Color.LightGray.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}