@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.feature.opd.presentation.model.TransactionDetails
import org.jetbrains.compose.resources.stringResource
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.appointment_details
import rotaryhospital.composeapp.generated.resources.back
import rotaryhospital.composeapp.generated.resources.charges
import rotaryhospital.composeapp.generated.resources.date
import rotaryhospital.composeapp.generated.resources.doctor
import rotaryhospital.composeapp.generated.resources.est_time
import rotaryhospital.composeapp.generated.resources.opd_id
import rotaryhospital.composeapp.generated.resources.order_id
import rotaryhospital.composeapp.generated.resources.patient_details
import rotaryhospital.composeapp.generated.resources.patient_id
import rotaryhospital.composeapp.generated.resources.patient_name
import rotaryhospital.composeapp.generated.resources.payment_details
import rotaryhospital.composeapp.generated.resources.payment_failed_message
import rotaryhospital.composeapp.generated.resources.payment_id
import rotaryhospital.composeapp.generated.resources.payment_pending_message
import rotaryhospital.composeapp.generated.resources.payment_result
import rotaryhospital.composeapp.generated.resources.refresh_status
import rotaryhospital.composeapp.generated.resources.share
import rotaryhospital.composeapp.generated.resources.specialization
import rotaryhospital.composeapp.generated.resources.status_failed
import rotaryhospital.composeapp.generated.resources.status_pending
import rotaryhospital.composeapp.generated.resources.status_successful
import rotaryhospital.composeapp.generated.resources.time
import rotaryhospital.composeapp.generated.resources.token
import rotaryhospital.composeapp.generated.resources.transaction_id
import rotaryhospital.composeapp.generated.resources.try_again

/**
 * Payment result screen that supports SUCCESS / PENDING / FAILED.
 * - SUCCESS: green header, details + Share action.
 * - PENDING: amber header, waiting message.
 * - FAILED : red header, error message + Try Again.
 */
@Composable
fun OpdPaymentResultScreen(
    transaction: TransactionDetails,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onRetry: () -> Unit
) {
    val scrollState = rememberScrollState()

    // determine normalized status
    val status = remember(transaction.paymentStatus) {
        when {
            transaction.paymentStatus.equals("SUCCESS", true) ||
                    transaction.paymentStatus.equals("PAYMENT_SUCCESS", true) -> Status.SUCCESS

            transaction.paymentStatus.equals("PENDING", true) ||
                    transaction.paymentStatus.equals("PAYMENT_PENDING", true) -> Status.PENDING

            else -> Status.FAILED
        }
    }

    // palette (kept local to avoid external deps)
    val primaryGreen = Color(0xFF16A34A)
    val amber = Color(0xFFF59E0B)
    val red = Color(0xFFDC2626)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF475569)

    val headerColor: Color
    val statusText: String
    val statusColor: Color
    val statusIcon = when (status) {
        Status.SUCCESS -> {
            headerColor = primaryGreen; statusText = stringResource(Res.string.status_successful); statusColor = primaryGreen
            Icons.Default.CheckCircle
        }
        Status.PENDING -> {
            headerColor = amber; statusText = stringResource(Res.string.status_pending); statusColor = amber
            Icons.Default.Info
        }
        Status.FAILED -> {
            headerColor = red; statusText = stringResource(Res.string.status_failed); statusColor = red
            Icons.Default.Clear
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.payment_result),
                        fontSize = 20.sp,
                        color = ColorPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                            tint = ColorPrimary
                        )
                    }
                },
                actions = {
                    if (status == Status.SUCCESS) {
                        IconButton(onClick = onShare) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(Res.string.share),
                                tint = ColorPrimary
                            )
                        }
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column {

                    // ---------- Header ----------
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                            .background(headerColor)
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = statusText,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // ---------- Body ----------
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

                        when (status) {
                            Status.SUCCESS -> {
                                // Top summary stats (Token / Est. Time / Charges)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatItem(
                                        label = stringResource(Res.string.token),
                                        value = transaction.tokenNumber.orDash(),
                                        alignment = Alignment.Start
                                    )
                                    StatItem(
                                        label = stringResource(Res.string.est_time),
                                        value = transaction.estimatedTime.orDash(),
                                        alignment = Alignment.CenterHorizontally
                                    )
                                    StatItem(
                                        label = stringResource(Res.string.charges),
                                        value = if (transaction.opdCharges.isNotBlank())
                                            "₹${transaction.opdCharges}" else "-",
                                        alignment = Alignment.End
                                    )
                                }

                                Spacer(Modifier.height(16.dp))
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                )

                                // Patient Details
                                Spacer(Modifier.height(14.dp))
                                SectionTitle(stringResource(Res.string.patient_details), textPrimary)
                                Spacer(Modifier.height(8.dp))
                                KeyRow(stringResource(Res.string.patient_name), transaction.patientName, textSecondary, textPrimary)
                                KeyRow(stringResource(Res.string.patient_id), transaction.patientId, textSecondary, textPrimary)

                                Spacer(Modifier.height(14.dp))
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                )

                                // Appointment Details
                                Spacer(Modifier.height(14.dp))
                                SectionTitle(stringResource(Res.string.appointment_details), textPrimary)
                                Spacer(Modifier.height(8.dp))
                                KeyRow(stringResource(Res.string.doctor), transaction.doctorName, textSecondary, textPrimary)
                                KeyRow(stringResource(Res.string.opd_id), transaction.opdId.orDash(), textSecondary, textPrimary)
                                KeyRow(stringResource(Res.string.date), transaction.registrationDate.orDash(), textSecondary, textPrimary)
                                KeyRow(stringResource(Res.string.time), transaction.estimatedTime.orDash(), textSecondary, textPrimary)
                                KeyRow(stringResource(Res.string.specialization), transaction.specialization, textSecondary, textPrimary)

                                Spacer(Modifier.height(14.dp))
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                )

                                // Payment Details
                                Spacer(Modifier.height(14.dp))
                                SectionTitle(stringResource(Res.string.payment_details), textPrimary)
                                Spacer(Modifier.height(8.dp))
                                KeyRow(
                                    stringResource(Res.string.transaction_id),
                                    transaction.transactionId.insertZeroWidthSpacesEvery(20),
                                    textSecondary,
                                    textPrimary
                                )
                                KeyRow(
                                    stringResource(Res.string.order_id),
                                    transaction.orderId.insertZeroWidthSpacesEvery(20),
                                    textSecondary,
                                    textPrimary
                                )
                                KeyRow(
                                    stringResource(Res.string.payment_id),
                                    transaction.paymentId.insertZeroWidthSpacesEvery(20),
                                    textSecondary,
                                    textPrimary
                                )
                            }

                            Status.PENDING -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(Res.string.payment_pending_message),
                                        fontSize = 16.sp,
                                        color = statusColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    CircularProgressIndicator(color = statusColor, strokeWidth = 3.dp)
                                    Spacer(Modifier.height(16.dp))
                                    KeyRow(
                                        stringResource(Res.string.transaction_id),
                                        transaction.transactionId.insertZeroWidthSpacesEvery(20),
                                        textSecondary,
                                        textPrimary
                                    )
                                    KeyRow(
                                        stringResource(Res.string.order_id),
                                        transaction.orderId.insertZeroWidthSpacesEvery(20),
                                        textSecondary,
                                        textPrimary
                                    )
                                    Spacer(Modifier.height(20.dp))
                                    TextButton(onClick = onRetry) {
                                        Text(stringResource(Res.string.refresh_status), color = ColorPrimary, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Status.FAILED -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = transaction.statusMessage ?: stringResource(Res.string.payment_failed_message),
                                        fontSize = 16.sp,
                                        color = statusColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    KeyRow(
                                        stringResource(Res.string.transaction_id),
                                        transaction.transactionId.insertZeroWidthSpacesEvery(20),
                                        textSecondary,
                                        textPrimary
                                    )
                                    KeyRow(
                                        stringResource(Res.string.order_id),
                                        transaction.orderId.insertZeroWidthSpacesEvery(20),
                                        textSecondary,
                                        textPrimary
                                    )
                                    Spacer(Modifier.height(20.dp))
                                    Button(
                                        onClick = onRetry,
                                        colors = ButtonDefaults.buttonColors(containerColor = statusColor)
                                    ) {
                                        Text(stringResource(Res.string.try_again), color = Color.White, fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    TextButton(onClick = onBack) {
                                        Text(stringResource(Res.string.back), color = ColorPrimary, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class Status { SUCCESS, PENDING, FAILED }

@Composable
private fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
private fun StatItem(label: String, value: String, alignment: Alignment.Horizontal) {
    Column(horizontalAlignment = alignment) {
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
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

/** Inserts zero-width spaces every n chars so very-long tokens can wrap */
private fun String.insertZeroWidthSpacesEvery(n: Int): String {
    if (length <= n) return this
    return chunked(n).joinToString("\u200B")
}

private fun String?.orDash(): String = this?.takeIf { it.isNotBlank() } ?: "-"
