package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.feature.opd.domain.model.DoctorAvailability
import com.rotary.hospital.feature.opd.domain.model.Leave
import com.rotary.hospital.feature.opd.presentation.viewmodel.DoctorAvailabilityViewModel
import com.rotary.hospital.feature.opd.presentation.viewmodel.UiState
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun DoctorAvailabilityScreen(
    doctorId: String,
    onBack: () -> Unit,
    viewModel: DoctorAvailabilityViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDoctorAvailability(doctorId)
    }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Doctor Availability",
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) { Text("Back", color = ColorPrimary) }
            }
            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is UiState.Loading -> CircularProgressIndicator(color = ColorPrimary)
                is UiState.Success -> {
                    val (availability, leaves) = (state as UiState.Success<Pair<List<DoctorAvailability>, List<Leave>>>).data
                    LazyColumn {
                        items(availability) { avail ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Days: ${avail.docDays}",
                                        color = ColorPrimary,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Time: ${avail.docTimeFrom} - ${avail.docTimeTo}",
                                        color = ColorPrimary,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                        if (leaves.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Leaves",
                                    color = ColorPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                            items(leaves) { leave ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "From: ${leave.cancelDate}",
                                            color = ColorPrimary,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "To: ${leave.cancelDateTo}",
                                            color = ColorPrimary,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Text(
                        text = (state as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                }

                UiState.Idle -> {}
            }
        }
    }
}