@file:OptIn(ExperimentalMaterial3Api::class)

package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import appicon.IconMale
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.feature.opd.presentation.viewmodel.SelectedOpdDetailsState
import com.rotary.hospital.feature.opd.presentation.viewmodel.SelectedOpdDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SelectedOpdDetailsScreen(
    opdId: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    viewModel: SelectedOpdDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(opdId) {
        viewModel.fetchOpdDetails(opdId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "OPD Details",
                        fontSize = 20.sp,
                        color = ColorPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
                            contentDescription = "Share",
                            tint = if (state is SelectedOpdDetailsState.Success) ColorPrimary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.38f
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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
                                    .semantics { contentDescription = "Loading OPD details" }
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Loading OPD details...",
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
                            .semantics { contentDescription = "Error loading OPD details" },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Error",
                            tint = ErrorRed,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Oops! Something went wrong",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = (state as SelectedOpdDetailsState.Error).message,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.fetchOpdDetails(opdId) },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                        ) {
                            Text("Try Again", fontWeight = FontWeight.Medium)
                        }
                    }
                }

                is SelectedOpdDetailsState.Success -> {
                    val opd = (state as SelectedOpdDetailsState.Success).opd

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            // ---------- Appointment Section ----------
                            DetailsSection(
                                title = "Appointment Details",
                                icon = Icons.Default.DateRange
                            ) {
                                KeyValueRow(
                                    label = "OPD ID",
                                    value = opd.opdId,
                                    copyable = true,
                                    clipboard = clipboard
                                )
                                KeyValueRow(label = "Date", value = opd.opdDate)
                                KeyValueRow(label = "Token", value = opd.tokenNumber)
                                KeyValueRow(label = "Estimated Time", value = opd.estimatedTime)
                                KeyValueRow(label = "Doctor", value = opd.doctor)
                                KeyValueRow(label = "Charges", value = opd.opdCharges)
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )

                            // ---------- Patient Section ----------
                            DetailsSection(title = "Patient Details", icon = Icons.Default.Person) {
                                KeyValueRow(
                                    label = "Name",
                                    value = opd.patientName,
                                    isImportant = true
                                )
                                KeyValueRow(
                                    label = "Patient ID",
                                    value = opd.patientId,
                                    copyable = true,
                                    clipboard = clipboard
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )

                            // ---------- Payment Section ----------
                            DetailsSection(title = "Payment Details", icon = Icons.Default.Info) {
                                KeyValueRow(
                                    label = "Transaction ID",
                                    value = opd.transactionId,
                                    small = true,
                                    copyable = true,
                                    clipboard = clipboard
                                )
                                KeyValueRow(label = "Message", value = opd.transactionMessage)
//                                KeyValueRow(label = "Order ID", value = opd.orderId, small = true, copyable = true, clipboard = clipboard)
//                                KeyValueRow(label = "Payment ID", value = opd.paymentId ?: "—", small = true, copyable = true, clipboard = clipboard)
                            }
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

/** Key-value row that adapts to screen width (no fixed label width) and supports copy action. */
@Composable
fun KeyValueRow(
    label: String,
    value: String,
    isImportant: Boolean = false,
    small: Boolean = false,
    copyable: Boolean = false,
    clipboard: androidx.compose.ui.platform.ClipboardManager? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .semantics { contentDescription = "$label: $value" },
        verticalAlignment = Alignment.Top
    ) {
        // label column (flexible)
        Text(
            text = "${label.uppercase()} :",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.36f)
        )

        Spacer(Modifier.width(8.dp))

        // value column (flexible)
        Row(
            modifier = Modifier.weight(0.64f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (copyable && clipboard != null) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Copy",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            clipboard.setText(AnnotatedString(value))
                        }
                        .padding(4.dp)
                )
            }
        }
    }
}

/** Section header with optional icon. (No divider inside — divider is handled between sections) */
@Composable
fun DetailsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ColorPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = ColorPrimary
            )
        }
        content()
    }
}
