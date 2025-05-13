package com.rotary.hospital

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.network.NetworkClient
import com.rotary.hospital.utils.Logger
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.logo

@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    onVerified: (Int) -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    AppTheme {
        val coroutineScope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        var resendEnabled by remember { mutableStateOf(false) }
        var countdown by remember { mutableStateOf(40) }

        LaunchedEffect(Unit) {
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            resendEnabled = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorPrimary)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(220.dp)
                        .height(201.dp)
                        .padding(10.dp)
                )

                // Title
                Text(
                    text = "Verify OTP",
                    color = White,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Instruction
                Text(
                    text = "Enter the 4-digit OTP sent to +91 $phoneNumber",
                    color = White,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // OTP Input Boxes
                val otpLength = 4
                val otpValues = remember { List(otpLength) { mutableStateOf("") } }
                val focusRequesters = remember { List(otpLength) { FocusRequester() } }
                val isFocused = remember { List(otpLength) { mutableStateOf(false) } }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    otpValues.forEachIndexed { index, otpValue ->
                        BasicTextField(
                            value = otpValue.value,
                            onValueChange = { newValue ->
                                if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                    otpValue.value = newValue
                                    if (newValue.isNotEmpty() && index < otpLength - 1) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else if (newValue.isEmpty() && index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(White)
                                .focusRequester(focusRequesters[index])
                                .onFocusChanged { isFocused[index].value = it.isFocused }
                                .padding(16.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 24.sp,
                                color = ColorPrimary,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (otpValue.value.isEmpty() && !isFocused[index].value) {
                                        Text(
                                            text = "-",
                                            color = ColorPrimary.copy(alpha = 0.5f),
                                            fontSize = 24.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                // Error Message
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Resend OTP
                TextButton(
                    onClick = {
                        if (resendEnabled) {
                            isLoading = true
                            errorMessage = ""
                            coroutineScope.launch {
                                try {
                                    val response = sendOtp(phoneNumber)
                                    if (response.response) {
                                        countdown = 40
                                        resendEnabled = false
                                        otpValues.forEach { it.value = "" }
                                        focusRequesters[0].requestFocus()
                                        onResend()
                                    } else {
                                        errorMessage = response.message
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Network error: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = resendEnabled && !isLoading
                ) {
                    Text(
                        text = if (resendEnabled) "Resend OTP" else "Resend OTP in $countdown sec",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verify Button
                Button(
                    onClick = {
                        val otp = otpValues.joinToString("") { it.value }
                        if (otp.length == otpLength) {
                            isLoading = true
                            errorMessage = ""
                            coroutineScope.launch {
                                try {
                                    val response = verifyOtp(phoneNumber, otp)
                                    if (response.response && response.verification == true) {
                                        onVerified(response.patients ?: 0)
                                    } else {
                                        errorMessage = "Invalid OTP"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Network error: ${e.message}"
                                    Logger.d("TAG",e.message.toString())
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            errorMessage = "Please enter a valid 4-digit OTP"
                        }
                    },
                    enabled = otpValues.all { it.value.isNotEmpty() } && !isLoading,
                    modifier = Modifier
                        .width(280.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = ColorPrimary,
                        disabledContainerColor = White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = ColorPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Verify", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Back Button
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .width(280.dp)
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
}


suspend fun verifyOtp(mobileNumber: String, otpCode: String): SmsVerificationResponse {
    val response = NetworkClient.httpClient.post("http://dev.erp.hospital/HMS_API/sms_verification.php") {
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(
            buildString {
                append("action=verify_otp")
                append("&mobile_number=$mobileNumber")
                append("&otp_code=$otpCode")
                append("&close=close")
            }
        )
    }
    val responseBody = response.bodyAsText()
    Logger.d("TAG", responseBody)

    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<SmsVerificationResponse>(responseBody)
}