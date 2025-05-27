package com.rotary.hospital

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.patient.registerPatient
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.common.appicon.IconCity
import com.rotary.hospital.core.common.appicon.IconMap
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onBack: () -> Unit, onCancel: () -> Unit, onSave: (String, String) -> Unit
) {
    // Dependencies
    val preferences: PreferencesManager = koinInject()
    val coroutineScope = rememberCoroutineScope()
    val savedMobileNumber by preferences.getString(PreferenceKeys.MOBILE_NUMBER, "")
        .collectAsState(initial = "")

    // State holders
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.Male) }
    var dob by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var guardianName by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf(Relation.SonOf) }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var bloodExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var fieldErrors by remember { mutableStateOf(mapOf<String, String>()) }
    var generalError by remember { mutableStateOf("") }

    // Button scale animation states
    var cancelButtonScale by remember { mutableStateOf(1f) }
    var saveButtonScale by remember { mutableStateOf(1f) }

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
                }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to previous screen",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White, titleContentColor = Color.Black
                )
            )
        }, containerColor = Color(0xFFF5F5F5), // Light gray background
        content = { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
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

                            // Full Name
                            InputField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    fieldErrors = fieldErrors - "fullName"
                                },
                                label = "Full Name",
                                leadingIcon = Icons.Default.Person,
                                errorMessage = fieldErrors["fullName"],
                                contentDescription = "Full name icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                                )

                            )
                            Spacer(Modifier.height(12.dp))

                            // Gender
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
                                        selected = gender == g,
                                        onClick = { gender = g },
                                        label = { Text(g.label, fontSize = 14.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = when (g) {
                                                    Gender.Male -> IconMale
                                                    Gender.Female -> IconFemale
                                                    else -> IconOther
                                                },
                                                contentDescription = "Gender ${g.label} icon",
                                                tint = if (gender == g) ColorPrimary.copy(alpha = 0.8f) else Color.Gray
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

                            // DOB
                            InputField(
                                value = dob,
                                onValueChange = {
                                    dob = it
                                    fieldErrors = fieldErrors - "dob"
                                },
                                label = "Date of Birth",
                                leadingIcon = Icons.Default.DateRange,
                                placeholder = "dd-mm-yyyy",
                                errorMessage = fieldErrors["dob"],
                                contentDescription = "Date of birth icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                                )
                            )
                            Spacer(Modifier.height(12.dp))

                            // Blood Group
                            ExposedDropdownMenuBox(
                                expanded = bloodExpanded,
                                onExpandedChange = { bloodExpanded = !bloodExpanded }) {
                                InputField(
                                    value = bloodGroup,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = "Blood Group",
                                    leadingIcon = IconDrop,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded)
                                    },
                                    errorMessage = fieldErrors["bloodGroup"],
                                    contentDescription = "Blood group icon",
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
                                            text = {
                                                Text(
                                                    group,
                                                    fontSize = 16.sp
                                                )
                                            },
                                            onClick = {
                                                bloodGroup = group
                                                bloodExpanded = false
                                                fieldErrors = fieldErrors - "bloodGroup"
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

                            // Guardian Name
                            InputField(
                                value = guardianName,
                                onValueChange = {
                                    guardianName = it
                                    fieldErrors = fieldErrors - "guardianName"
                                },
                                label = "Guardian Name",
                                leadingIcon = IconGuardian,
                                errorMessage = fieldErrors["guardianName"],
                                contentDescription = "Guardian name icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                                )
                            )
                            Spacer(Modifier.height(12.dp))

                            // Relation
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
                                        modifier = Modifier.fillMaxWidth()
                                            .clickable { relation = rel }.padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        RadioButton(
                                            selected = relation == rel,
                                            onClick = { relation = rel },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = ColorPrimary.copy(alpha = 0.8f),
                                                unselectedColor = Color.Gray
                                            ),
                                            modifier = Modifier.semantics {
                                                contentDescription = "Select relation: ${rel.label}"
                                            })
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

                            // Email
                            InputField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    fieldErrors = fieldErrors - "email"
                                },
                                label = "Email",
                                leadingIcon = Icons.Default.Email,
                                errorMessage = fieldErrors["email"],
                                contentDescription = "Email icon",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                                )
                            )
                            Spacer(Modifier.height(12.dp))

                            // Address
                            InputField(
                                value = address,
                                onValueChange = {
                                    address = it
                                    fieldErrors = fieldErrors - "address"
                                },
                                label = "Address",
                                leadingIcon = Icons.Default.Home,
                                errorMessage = fieldErrors["address"],
                                contentDescription = "Address icon",
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                                )
                            )
                            Spacer(Modifier.height(12.dp))

                            // City and State
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                InputField(
                                    value = city,
                                    onValueChange = {
                                        city = it
                                        fieldErrors = fieldErrors - "city"
                                    },
                                    label = "City",
                                    leadingIcon = IconCity,
                                    errorMessage = fieldErrors["city"],
                                    contentDescription = "City icon",
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                                    )
                                )
                                InputField(
                                    value = state,
                                    onValueChange = {
                                        state = it
                                        fieldErrors = fieldErrors - "state"
                                    },
                                    label = "State",
                                    leadingIcon = IconMap,
                                    errorMessage = fieldErrors["state"],
                                    contentDescription = "State icon",
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // General error message
                    AnimatedVisibility(
                        visible = generalError.isNotEmpty(), enter = fadeIn(), exit = fadeOut()
                    ) {
                        Text(
                            text = generalError,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(Modifier.weight(1f)) // Push buttons to bottom

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth()
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
                                containerColor = ErrorRed, contentColor = White
                            ),
                            modifier = Modifier.weight(1f).height(56.dp)
                                .scale(cancelButtonScale)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Cancel registration icon",
                                tint = White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Cancel", fontSize = 16.sp, fontWeight = FontWeight.Medium
                            )
                        }
                        ElevatedButton(
                            onClick = {
                                saveButtonScale = 0.95f
                                val validationErrors = validateInputs(
                                    fullName,
                                    guardianName,
                                    dob,
                                    bloodGroup,
                                    email,
                                    address,
                                    city,
                                    state
                                )
                                if (validationErrors == null) {
                                    isLoading = true
                                    fieldErrors = emptyMap()
                                    generalError = ""
                                    coroutineScope.launch {
                                        try {
                                            val response = registerPatient(
                                                mobileNumber = savedMobileNumber,
                                                name = fullName,
                                                guardianType = relation.toApiString(),
                                                guardianName = guardianName,
                                                gender = gender.label,
                                                age = dob,
                                                bloodGroup = bloodGroup,
                                                email = email,
                                                address = address,
                                                city = city,
                                                state = state
                                            )
                                            if (response.response && response.data != null) {
                                                val patient = response.data.firstOrNull()
                                                if (patient != null) {
                                                    preferences.saveString(
                                                        PreferenceKeys.PATIENT_ID, patient.pid1
                                                    )
                                                    preferences.saveString(
                                                        PreferenceKeys.PATIENT_NAME, patient.p_name
                                                    )
                                                    preferences.saveBoolean(
                                                        PreferenceKeys.IS_LOGGED_IN, true
                                                    )
                                                    preferences.saveString(
                                                        PreferenceKeys.MOBILE_NUMBER,
                                                        savedMobileNumber
                                                    )
                                                    onSave(patient.pid1, patient.p_name)
                                                } else {
                                                    generalError = "No patient data received"
                                                }
                                            } else {
                                                generalError = "Registration failed"
                                            }
                                        } catch (e: Exception) {
                                            generalError = "Network error: ${e.message}"
                                            Logger.e("RegistrationScreen", "Error: ${e.message}", e)
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    fieldErrors = validationErrors
                                    generalError = ""
                                }
                                saveButtonScale = 1f
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorPrimary, contentColor = White
                            ),
                            modifier = Modifier.weight(1f).height(56.dp)
                                .scale(saveButtonScale)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            if (isLoading) {
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
                                    "Save", fontSize = 16.sp, fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp)) // Ensure button elevation is visible
                }

                // Loading overlay
                AnimatedVisibility(
                    visible = isLoading, enter = fadeIn(), exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))
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

// Reusable input field component
@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    errorMessage: String? = null,
    contentDescription: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    placeholder: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1
) {
    Column(modifier = modifier.padding(bottom = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = contentDescription,
                    tint = if (errorMessage != null) ErrorRed else ColorPrimary.copy(alpha = 0.8f)
                )
            },
            trailingIcon = trailingIcon,
            placeholder = placeholder?.let { { Text(it, fontSize = 14.sp) } },
            readOnly = readOnly,
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorPrimary,
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = ErrorRed,
                focusedLabelColor = ColorPrimary,
                unfocusedLabelColor = Color.Gray,
                errorLabelColor = ErrorRed,
                cursorColor = ColorPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            keyboardOptions = keyboardOptions,
            maxLines = maxLines
        )
        AnimatedVisibility(
            visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()
        ) {
            errorMessage?.let {
                Text(
                    text = it,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 2.dp).sizeIn(maxHeight = 20.dp)
                )
            }
        }
    }
}

// Updated validation to return field-specific errors
private fun validateInputs(
    fullName: String,
    guardianName: String,
    dob: String,
    bloodGroup: String,
    email: String,
    address: String,
    city: String,
    state: String
): Map<String, String>? {
    val errors = mutableMapOf<String, String>()
    if (fullName.isEmpty()) errors["fullName"] = "Please enter a valid name"
    if (guardianName.isEmpty()) errors["guardianName"] = "Please enter a valid guardian name"
    if (dob.isEmpty()) errors["dob"] = "Please enter a valid date of birth"
    if (bloodGroup.isEmpty()) errors["bloodGroup"] = "Please select a blood group"
    if (email.isEmpty() || !isValidEmail(email)) errors["email"] =
        "Please enter a valid email address"
    if (address.isEmpty()) errors["address"] = "Please enter a valid address"
    if (city.isEmpty()) errors["city"] = "Please enter a valid city"
    if (state.isEmpty()) errors["state"] = "Please enter a valid state"
    return if (errors.isEmpty()) null else errors
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    )
    return emailRegex.matches(email)
}

enum class Gender(val label: String) {
    Male("Male"), Female("Female"), Other("Other")
}

enum class Relation(val label: String) {
    SonOf("Son of"), DaughterOf("Daughter of"), WifeOf("Wife of"), Other("Other");

    fun toApiString(): String = when (this) {
        SonOf -> "S/O"
        DaughterOf -> "D/O"
        WifeOf -> "W/O"
        Other -> "Other"
    }
}