package com.rotary.hospital.feature.opd.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.feature.opd.presentation.model.TransactionDetails

@Composable
fun OpdPaymentResultScreen(
    transactionDetails: TransactionDetails,
    onShareScreenshot: () -> Unit,
    onBack: () -> Unit
) {


    AppTheme {
        Column(
            modifier = Modifier.Companion.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = transactionDetails.paymentStatus,
                    color = ColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Companion.Bold
                )
                TextButton(onClick = onBack) {
                    Text("Back", color = ColorPrimary)
                }
            }
            Spacer(modifier = Modifier.Companion.height(16.dp))

            Text(
                text = "Thank you for your payment!",
                color = ColorPrimary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.Companion.height(16.dp))

            Text(
                text = transactionDetails.patientName,
                color = ColorPrimary,
                fontSize = 16.sp
            )
        }

    }
}