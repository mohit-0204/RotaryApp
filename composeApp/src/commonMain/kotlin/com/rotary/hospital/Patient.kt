package com.rotary.hospital

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rotary.hospital.network.NetworkClient
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.util.logging.Logger
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
    val data: List<ApiPatient> // Use Patient instead of ApiPatient if using Option 1
)

@Composable
fun PatientListScreen(phoneNumber: String, navController: NavController) {
    val patients = remember { mutableStateOf<List<Patient>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = NetworkClient.httpClient.get("http://rotaryapp.mdimembrane.com/HMS_API/patient_data.php") {
                    parameter("action", "get_registered_patients")
                    parameter("mobile_number", phoneNumber)
                }
                val json = Json { ignoreUnknownKeys = true }
                val responseText = response.bodyAsText()
                com.rotary.hospital.utils.Logger.d("tag", responseText)

                // Parse the JSON into PatientResponse
                val patientResponse = json.decodeFromString<PatientResponse>(responseText)

                // Check if the response is successful
                if (patientResponse.response == "true") {
                    // Map ApiPatient to Patient, using phoneNumber from parameter
                    patients.value = patientResponse.data.map { apiPatient ->
                        Patient(
                            id = apiPatient.id,
                            name = apiPatient.name,
                            phoneNumber = phoneNumber // Use the input phoneNumber
                        )
                    }
                } else {
                    errorMessage.value = "API Error: ${patientResponse.message}"
                }
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = "Error fetching patients: ${e.message}"
                com.rotary.hospital.utils.Logger.e("tag", "Error: ${e.message}", e)
                isLoading.value = false
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
//                    navController.navigate(AppRoute.RegisterNewPatient(phoneNumber))
                          },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Patient", tint = Color.White)
            }
        }
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
                patients.value.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No patients registered.", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
//                            navController.navigate(AppRoute.RegisterNewPatient(phoneNumber))
                        }) {
                            Text("Register New Patient")
                        }
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(patients.value) { patient ->
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

@Composable
fun PatientListItem(patient: Patient, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Patient Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = patient.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ID: ${patient.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}