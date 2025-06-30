package com.rotary.hospital.feature.auth.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.ui.component.CustomKeyboard
import com.rotary.hospital.feature.auth.domain.usecase.SendOtpUseCase
import com.rotary.hospital.feature.auth.presentation.viewmodel.OtpAction
import com.rotary.hospital.feature.auth.presentation.viewmodel.OtpVerificationState
import com.rotary.hospital.feature.auth.presentation.viewmodel.OtpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    onVerified: (Int) -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit,
    viewModel: OtpViewModel = koinViewModel(),
    sendOtpUseCase: SendOtpUseCase = koinInject()
) {
    AppTheme {
        val coroutineScope = rememberCoroutineScope()
        var resendEnabled by remember { mutableStateOf(false) }
        var countdown by remember { mutableStateOf(40) }
        val otpState by viewModel.otpState.collectAsState()
        val state by viewModel.state.collectAsState()

        // state for modal sheet
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { newState ->
                newState != SheetValue.Hidden //  Stop bottom sheet from hiding on outside press
            }
        )

        // set mobile + start countdown
        LaunchedEffect(Unit) {
            viewModel.setMobileNumber(phoneNumber)
        }
        LaunchedEffect(countdown) {
            if (countdown > 0) {
                while (countdown > 0) {
                    delay(1_000)
                    countdown--
                }
                resendEnabled = true
            }
        }

        // hide system keyboard when all digits entered
        LaunchedEffect(state.code) {
            if (state.code.none { it == null }) {
                coroutineScope.launch {
                    sheetState.hide()
                }
                viewModel.verifyOtp()
            }
        }

        // forward success event
        LaunchedEffect(otpState) {
            if (otpState is OtpVerificationState.Success) {
                val response = (otpState as OtpVerificationState.Success).response
                onVerified(response.patients ?: 0)
            }
        }

        // only compose the sheet when it’s shown
        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    /* do nothing */
                },
                sheetState = sheetState,
                dragHandle = null,             // remove the drag‑handle + its space
                scrimColor = Color.Transparent, // remove the dimming overlay
                containerColor = White
            ) {
                CustomKeyboard(
                    onNumberClick = { digit ->
                        state.focusedIndex?.let { idx ->
                            viewModel.onAction(OtpAction.OnEnterNumber(digit, idx))
                        }
                    },
                    onBackspaceClick = {
                        viewModel.onAction(OtpAction.OnKeyBoardBack)
                    },
                    onDoneClick = {
                        coroutineScope.launch { sheetState.hide() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .height(300.dp)
                )
            }
        }

        // main content
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo & Title
            androidx.compose.foundation.Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "Logo",
                colorFilter = ColorFilter.tint(ColorPrimary),
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .padding(10.dp)
            )

            Text(
                text = "OTP Verification",
                color = ColorPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Enter the 4-digit OTP sent to +91 $phoneNumber",
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // OTP Boxes Row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                state.code.forEachIndexed { index, digit ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(White, RoundedCornerShape(8.dp))
                            .border(
                                2.dp,
                                if (state.focusedIndex == index) ColorPrimary else Color.Gray,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.onAction(OtpAction.OnChangeFieldFocus(index))
                                coroutineScope.launch { sheetState.show() }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit?.toString() ?: "-",
                            fontSize = 32.sp,
                            color = ColorPrimary
                        )
                    }
                }
            }

            // Error message
            if (otpState is OtpVerificationState.Error) {
                Text(
                    text = (otpState as OtpVerificationState.Error).message,
                    color = ErrorRed,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Resend OTP
            TextButton(
                onClick = {
                    if (resendEnabled) {
                        coroutineScope.launch {
                            viewModel.onAction(OtpAction.ClearFields)
                            viewModel.resendOtp()
                            countdown = 40
                            resendEnabled = false
                            onResend()
                        }
                    }
                },
                enabled = resendEnabled && otpState !is OtpVerificationState.Loading
            ) {
                Text(
                    text = if (resendEnabled) "Resend OTP" else "Resend in $countdown sec",
                    color = ColorPrimary
                )
            }

            Spacer(Modifier.height(16.dp))

            // Verify Button
            Button(
                onClick = { viewModel.verifyOtp() },
                enabled = state.code.all { it != null } && otpState !is OtpVerificationState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimary,
                    contentColor = White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = White
                )
            ) {
                if (otpState is OtpVerificationState.Loading) {
                    CircularProgressIndicator(
                        color = White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Verify", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Back Button
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed,
                    contentColor = White
                )
            ) {
                Text("Back", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
