@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import appicon.IconMale
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.feature.opd.presentation.viewmodel.SelectedOpdDetailsState
import com.rotary.hospital.feature.opd.presentation.viewmodel.SelectedOpdDetailsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.back
import rotaryhospital.composeapp.generated.resources.charges
import rotaryhospital.composeapp.generated.resources.child
import rotaryhospital.composeapp.generated.resources.date
import rotaryhospital.composeapp.generated.resources.doctor
import rotaryhospital.composeapp.generated.resources.error
import rotaryhospital.composeapp.generated.resources.error_loading_opd_details
import rotaryhospital.composeapp.generated.resources.est_time
import rotaryhospital.composeapp.generated.resources.name
import rotaryhospital.composeapp.generated.resources.oops_something_went_wrong
import rotaryhospital.composeapp.generated.resources.opd_details_title
import rotaryhospital.composeapp.generated.resources.opd_id
import rotaryhospital.composeapp.generated.resources.opd_patient_registration_title
import rotaryhospital.composeapp.generated.resources.patient_details
import rotaryhospital.composeapp.generated.resources.patient_id
import rotaryhospital.composeapp.generated.resources.payment_details
import rotaryhospital.composeapp.generated.resources.share
import rotaryhospital.composeapp.generated.resources.status
import rotaryhospital.composeapp.generated.resources.successful
import rotaryhospital.composeapp.generated.resources.token
import rotaryhospital.composeapp.generated.resources.transaction_id
import rotaryhospital.composeapp.generated.resources.try_again

@Composable
fun SelectedOpdDetailsScreen(
    opdId: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    viewModel: SelectedOpdDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(opdId) {
        viewModel.fetchOpdDetails(opdId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.opd_details_title),
                        fontSize = 20.sp,
                        color = ColorPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = stringResource(Res.string.back),
                            tint = ColorPrimary
                        )
                    }
                },
                actions = {
                    // Show share only when success (keeps UI clear)
                    IconButton(
                        onClick = onShare,
                        enabled = state is SelectedOpdDetailsState.Success
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(Res.string.share),
                            tint = if (state is SelectedOpdDetailsState.Success) ColorPrimary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.38f
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color(0xFFF9F9F9))
                .padding(16.dp)
        ) {
            when (state) {
                is SelectedOpdDetailsState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = ColorPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                                    .semantics {
                                        contentDescription =
                                            Res.string.opd_details_title.toString()
                                    }
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                stringResource(Res.string.opd_details_title),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is SelectedOpdDetailsState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .semantics {
                                contentDescription =
                                    Res.string.error_loading_opd_details.toString()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.error),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(Res.string.oops_something_went_wrong),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = (state as SelectedOpdDetailsState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.fetchOpdDetails(opdId) },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                        ) {
                            Text(stringResource(Res.string.try_again), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                is SelectedOpdDetailsState.Success -> {
                    val opd = (state as SelectedOpdDetailsState.Success).opd

                    // palette taken from your HTML
                    val primaryColor = Color(0xFF0D9488)
                    val primaryLight = Color(0xFFF0FDFA)
                    val textPrimary = Color(0xFF1E293B)
                    val textSecondary = Color(0xFF475569)
                    val childColor = Color(0xFF1D4ED8)
                    val successGreen = Color(0xFF16A34A)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column {

                            // ---------- Top green header ----------
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                    .background(primaryColor)
                                    .padding(horizontal = 20.dp, vertical = 18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        // small label (like the HTML "OPD ID")
                                        Text(
                                            text = stringResource(Res.string.opd_id),
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.92f)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            text = opd.opdId,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = stringResource(Res.string.date),
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.92f)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            text = opd.opdDate,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // ---------- main white body ----------
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 20.dp,
                                    vertical = 16.dp
                                )
                            ) {

                                // Doctor row + capsule on right
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = stringResource(Res.string.doctor),
                                            fontSize = 13.sp,
                                            color = textSecondary
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = opd.doctor,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textPrimary
                                        )
                                    }

                                    // Child capsule
                                    Box(
                                        modifier = Modifier
                                            .background(childColor, shape = RoundedCornerShape(50))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.child),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // Token / Est. Time / Charges (3 columns)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatItem(
                                        label = stringResource(Res.string.token),
                                        value = opd.tokenNumber,
                                        modifier = Modifier.weight(1f),
                                        alignment = Alignment.Start
                                    )
                                    StatItem(
                                        label = stringResource(Res.string.est_time),
                                        value = opd.estimatedTime,
                                        modifier = Modifier.weight(1f),
                                        alignment = Alignment.CenterHorizontally

                                    )
                                    StatItem(
                                        label = stringResource(Res.string.charges),
                                        value = "₹${opd.opdCharges}",
                                        modifier = Modifier.weight(1f),
                                        alignment = Alignment.End

                                    )
                                }
                            }

                            // divider
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )

                            // ---------- Patient Details section ----------
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 20.dp,
                                    vertical = 14.dp
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                primaryLight,
                                                shape = RoundedCornerShape(50)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = stringResource(Res.string.patient_details),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                Column {
                                    KeyRow(
                                        label = stringResource(Res.string.name),
                                        value = opd.patientName,
                                        labelColor = textSecondary,
                                        valueColor = textPrimary
                                    )
                                    KeyRow(
                                        label = stringResource(Res.string.patient_id),
                                        value = opd.patientId,
                                        labelColor = textSecondary,
                                        valueColor = textPrimary
                                    )
                                }
                            }

                            // divider
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )

                            // ---------- Payment Details section ----------
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 20.dp,
                                    vertical = 14.dp
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                primaryLight,
                                                shape = RoundedCornerShape(50)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = stringResource(Res.string.payment_details),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                // Transaction ID (wraps with zero-width spaces to emulate break-all)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = stringResource(Res.string.transaction_id),
                                        fontSize = 14.sp,
                                        color = textSecondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = opd.transactionId.insertZeroWidthSpacesEvery(20),
                                        fontSize = 14.sp,
                                        color = textPrimary,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp),
                                        textAlign = TextAlign.End
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                // Status row (green icon + Successful)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(Res.string.status),
                                        fontSize = 14.sp,
                                        color = textSecondary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = successGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(Res.string.successful),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = successGreen
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }


                is SelectedOpdDetailsState.Idle -> {
                    // No-op
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF475569))
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun KeyRow(label: String, value: String, labelColor: Color, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 14.sp, color = labelColor, modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

/** Inserts zero-width spaces every n chars so very-long tokens can wrap  */
private fun String.insertZeroWidthSpacesEvery(n: Int): String {
    if (this.length <= n) return this
    return this.chunked(n).joinToString("\u200B")
}
