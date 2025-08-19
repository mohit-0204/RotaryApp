package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.feature.opd.presentation.viewmodel.SelectedOpdDetailsState
import com.rotary.hospital.feature.opd.presentation.viewmodel.SelectedOpdDetailsViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SelectedOpdDetailsScreen(
    opdId: String,
    onBack: () -> Unit,
    viewModel: SelectedOpdDetailsViewModel = koinViewModel(),
    preferences: PreferencesManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    var mobileNumber by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").collect { savedMobile ->
            if (savedMobile.isNotBlank()) {
                mobileNumber = savedMobile
                viewModel.fetchOpdDetails(savedMobile, opdId)
            }
        }
    }

    AppTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OPD Details",
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) {
                    Text("Back", color = ColorPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (state) {
                is SelectedOpdDetailsState.Loading -> CircularProgressIndicator(color = ColorPrimary)
                is SelectedOpdDetailsState.Success -> {
                    val opd = (state as SelectedOpdDetailsState.Success).opd
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "OPD ID: ${opd.opdId}",
                                color = ColorPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Type: ${opd.opdType}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Patient: ${opd.patientName}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Doctor: ${opd.doctor}",
                                color = ColorPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Date: ${opd.date}", color = ColorPrimary, fontSize = 16.sp
                            )
                        }
                    }
                }

                is SelectedOpdDetailsState.Error -> {
                    Text(
                        text = (state as SelectedOpdDetailsState.Error).message,
                        color = ErrorRed,
                        fontSize = 16.sp
                    )
                }

                is SelectedOpdDetailsState.Idle -> {}
            }
        }
    }
}