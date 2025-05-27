package com.rotary.hospital

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.data.model.Patient
import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.core.theme.ColorPrimary
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json

@Serializable
data class ApiPatient(
    @SerialName("pid1") val id: String,
    @SerialName("p_name") val name: String
)

@Serializable
data class PatientResponse(
    val response: String,
    val message: String,
    val data: List<ApiPatient>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(phoneNumber: String, onAddPatient: () -> Unit, onBackClick: () -> Unit) {
    val patients = remember { mutableStateOf<List<Patient>>(emptyList()) }
    val filteredPatients = remember { mutableStateOf<List<Patient>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val instructionAlpha = remember { Animatable(1f) }
    var blinkCount by remember { mutableStateOf(0) }

    // Blinking animation for instruction text using alpha
    LaunchedEffect(Unit) {
        while (blinkCount < 3) {
            instructionAlpha.animateTo(0f, animationSpec = tween(500)) // Fade out
            instructionAlpha.animateTo(1f, animationSpec = tween(500)) // Fade in
            blinkCount++
        }
    }

    // Fetch patients
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response =
                    NetworkClient.httpClient.get("http://rotaryapp.mdimembrane.com/HMS_API/patient_data.php") {
                        parameter("action", "get_registered_patients")
                        parameter("mobile_number", phoneNumber)
                    }
                val json = Json { ignoreUnknownKeys = true }
                val responseText = response.bodyAsText()
                com.rotary.hospital.core.common.Logger.d("tag", responseText)

                val patientResponse = json.decodeFromString<PatientResponse>(responseText)

                if (patientResponse.response == "true") {
                    patients.value = patientResponse.data.map { apiPatient ->
                        Patient(
                            id = apiPatient.id,
                            name = apiPatient.name,
                            phoneNumber = phoneNumber
                        )
                    }
                    filteredPatients.value = patients.value
                } else {
                    errorMessage.value = "API Error: ${patientResponse.message}"
                }
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = "Error fetching patients: ${e.message}"
                com.rotary.hospital.core.common.Logger.e("tag", "Error: ${e.message}", e)
                isLoading.value = false
            }
        }
    }

    // Filter patients based on search query
    LaunchedEffect(searchQuery, patients.value) {
        filteredPatients.value = if (searchQuery.isEmpty()) {
            patients.value
        } else {
            patients.value.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.id.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!isSearchActive) {
                        Text(
                            "Registered Patients",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ColorPrimary
                        )
                    } else {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search patients...") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorPrimary,
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = ColorPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSearchActive) {
                            isSearchActive = false
                            searchQuery = ""
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isSearchActive) "Cancel search" else "Back to previous screen",
                            tint = ColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search patients",
                                tint = ColorPrimary.copy(alpha = 0.8f)
                            )
                        }
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
                onClick = onAddPatient,
                containerColor = ColorPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Patient", tint = Color.White)
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading.value -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage.value.isNotEmpty() -> {
                    Text(
                        text = errorMessage.value,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                filteredPatients.value.isEmpty() && patients.value.isNotEmpty() -> {
                    Text(
                        text = "No patients match your search.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                patients.value.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No patients found.", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onAddPatient) {
                            Text("Register New Patient")
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(shape = RoundedCornerShape(12.dp))
                                .background(ColorPrimary.copy(alpha = 0.1f))
                                .padding(16.dp)
                                .alpha(instructionAlpha.value),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tap on already registered patients to proceed or create a new patient by clicking + button",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ColorPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredPatients.value) { patient ->
                                PatientListItem(patient = patient, onClick = {
                                    // Navigate to patient details or handle selection
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientListItem(patient: Patient, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ColorPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = patient.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patient.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.Black
                    )

                    Text(
                        text = "ID: ${patient.id}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                        color = Color.Gray
                    )

                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Select patient",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(ColorPrimary.copy(alpha = 0.5f))
            )
        }
    }
}