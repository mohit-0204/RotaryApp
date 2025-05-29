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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
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
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.component.InputField
import com.rotary.hospital.feature.patient.presentation.viewmodel.Gender
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientRegistrationState
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientRegistrationViewModel
import com.rotary.hospital.feature.patient.presentation.viewmodel.RegistrationFormState
import com.rotary.hospital.feature.patient.presentation.viewmodel.Relation
import org.koin.compose.viewmodel.koinViewModel
import appicon.IconDrop
import appicon.IconFemale
import appicon.IconGuardian
import appicon.IconMale
import appicon.IconOther
import com.rotary.hospital.core.common.appicon.IconCity
import com.rotary.hospital.core.common.appicon.IconMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onSave: (String) -> Unit,
    viewModel: PatientRegistrationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val formState by viewModel.formState.collectAsState()
    var bloodExpanded by remember { mutableStateOf(false) }
    var cancelButtonScale by remember { mutableStateOf(1f) }
    var saveButtonScale by remember { mutableStateOf(1f) }

    LaunchedEffect(state) {
        if (state is PatientRegistrationState.Success) {
            onSave((state as PatientRegistrationState.Success).patient.name)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Register New Patient",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
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
        containerColor = Color(0xFFF5F5F5),
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
                                "Personal Info",
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
                                label = "Full Name",
                                leadingIcon = Icons.Default.Person,
                                errorMessage = formState.fieldErrors["fullName"],
                                contentDescription = "Full name icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                            Spacer(Modifier.height(12.dp))

                            Text(
                                "Select Gender",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Gender.entries.forEach { g ->
                                    FilterChip(
                                        selected = formState.gender == g,
                                        onClick = {
                                            viewModel.updateFormState(formState.copy(gender = g))
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
                                                tint = if (formState.gender == g) ColorPrimary.copy(alpha = 0.8f) else Color.Gray
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

                            InputField(
                                value = formState.dob,
                                onValueChange = {
                                    viewModel.updateFormState(formState.copy(dob = it))
                                },
                                label = "Date of Birth",
                                leadingIcon = Icons.Default.DateRange,
                                placeholder = "dd-mm-yyyy",
                                errorMessage = formState.fieldErrors["dob"],
                                contentDescription = "Date of birth icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                            Spacer(Modifier.height(12.dp))

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
                                    listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").forEach { group ->
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
                                "Guardian Info",
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
                                label = "Guardian Name",
                                leadingIcon = IconGuardian,
                                errorMessage = formState.fieldErrors["guardianName"],
                                contentDescription = "Guardian name icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
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
                                "Contact Info",
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
                            Spacer(Modifier.height(12.dp))

                            InputField(
                                value = formState.address,
                                onValueChange = {
                                    viewModel.updateFormState(formState.copy(address = it))
                                },
                                label = "Address",
                                leadingIcon = Icons.Default.Home,
                                errorMessage = formState.fieldErrors["address"],
                                contentDescription = "Address icon",
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
                                    label = "City",
                                    leadingIcon = IconCity,
                                    errorMessage = formState.fieldErrors["city"],
                                    contentDescription = "City icon",
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
                                    label = "State",
                                    leadingIcon = IconMap,
                                    errorMessage = formState.fieldErrors["state"],
                                    contentDescription = "State icon",
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
                        visible = state is PatientRegistrationState.Error,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (state is PatientRegistrationState.Error) {
                            Text(
                                text = (state as PatientRegistrationState.Error).message,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                cancelButtonScale = 0.95f
                                onCancel()
                                cancelButtonScale = 1f
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ErrorRed,
                                contentColor = White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .scale(cancelButtonScale)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = state !is PatientRegistrationState.Loading,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Cancel registration icon",
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
                                .scale(saveButtonScale)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = state !is PatientRegistrationState.Loading,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            if (state is PatientRegistrationState.Loading) {
                                CircularProgressIndicator(
                                    color = White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Save registration icon",
                                    tint = White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Save",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                AnimatedVisibility(
                    visible = state is PatientRegistrationState.Loading,
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