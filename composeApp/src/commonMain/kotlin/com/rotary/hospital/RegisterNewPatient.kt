package com.rotary.hospital

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onSave: (RegistrationData) -> Unit
) {
    // Colors
    val primaryColor = Color(0xFF00897B)
    val cancelColor = Color(0xFFFF6F61)

    // State holders
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.Male) }
    var dob by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var guardianName by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf(Relation.SonOf) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var bloodExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registered Patients List") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        content = { padding ->
            // Use a Column for layout: a scrollable content area and a fixed bottom row
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Scrollable fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Personal Info Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.background(White).padding(16.dp)) {
                            Text("Personal Info", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))

                            // Full Name
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(12.dp))

                            // Gender Toggle
                            Text("Gender", fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Gender.values().forEach { g ->
                                    FilterChip(
                                        selected = (gender == g),
                                        onClick = { gender = g },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = when(g) {
                                                    Gender.Male -> Icons.Default.Person
                                                    Gender.Female -> Icons.Default.Person
                                                    else -> Icons.Default.Person
                                                },
                                                contentDescription = null
                                            )
                                        },
                                        label = { Text(g.label) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                            // DOB
                            OutlinedTextField(
                                value = dob,
                                onValueChange = { dob = it },
                                label = { Text("Date of Birth") },
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                                placeholder = { Text("dd-mm-yyyy") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(12.dp))

                            // Blood Group Dropdown
                            ExposedDropdownMenuBox(
                                expanded = bloodExpanded,
                                onExpandedChange = { bloodExpanded = !bloodExpanded }
                            ) {
                                OutlinedTextField(
                                    value = bloodGroup,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Blood Group") },
                                    leadingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = bloodExpanded,
                                    onDismissRequest = { bloodExpanded = false }
                                ) {
                                    listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
                                        .forEach { group ->
                                            DropdownMenuItem(
                                                text = { Text(group) },
                                                onClick = {
                                                    bloodGroup = group
                                                    bloodExpanded = false
                                                }
                                            )
                                        }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Guardian Info Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.background(White).padding(16.dp)) {
                            Text("Guardian Info", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))

                            // Guardian Name
                            OutlinedTextField(
                                value = guardianName,
                                onValueChange = { guardianName = it },
                                label = { Text("Guardian Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(12.dp))

                            // Relation
                            Text("Relation to Guardian", fontWeight = FontWeight.Medium)
                            Column {
                                Relation.values().forEach { rel ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = (relation == rel),
                                            onClick = { relation = rel }
                                        )
                                        Text(rel.label)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Contact Info Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.background(White).padding(16.dp)) {
                            Text("Contact Info", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Address") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 3
                            )
                            Spacer(Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("City") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = state,
                                    onValueChange = { state = it },
                                    label = { Text("State") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }

                // Action Buttons (fixed at bottom)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = cancelColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onSave(
                                RegistrationData(
                                    fullName,
                                    gender,
                                    dob,
                                    bloodGroup,
                                    guardianName,
                                    relation,
                                    email,
                                    phone,
                                    address,
                                    city,
                                    state
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save")
                    }
                }
            }
        }
    )
}

// Supporting enums and data class
enum class Gender(val label: String) { Male("Male"), Female("Female"), Other("Other") }
enum class Relation(val label: String) { SonOf("Son of"), DaughterOf("Daughter of"), WifeOf("Wife of"), Other("Other") }
data class RegistrationData(
    val fullName: String,
    val gender: Gender,
    val dob: String,
    val bloodGroup: String,
    val guardianName: String,
    val relation: Relation,
    val email: String,
    val phone: String,
    val address: String,
    val city: String,
    val state: String
)
