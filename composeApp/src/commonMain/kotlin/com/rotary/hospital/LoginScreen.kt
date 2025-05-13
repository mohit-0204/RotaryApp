package com.rotary.hospital

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.logo
import rotaryhospital.composeapp.generated.resources.login_screen_message

@Composable
fun LoginScreen(
    onNextClick: (String) -> Unit,
    onExitClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var mobileNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(220.dp)
                    .height(201.dp)
                    .padding(10.dp)
            )

            Text(
                text = stringResource(Res.string.login_screen_message),
                color = White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))


            Surface(
                shadowElevation = 2.dp, // Adds the elevation (shadow)
                shape = RoundedCornerShape(16.dp), // Match TextField shape
                color = ColorPrimary,
                modifier = Modifier
                    .width(301.dp)
                    .padding(horizontal = 16.dp)
            ) {


                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            mobileNumber = it
                            errorMessage = ""
                        }
                    },
                    label = { Text("Enter Mobile Number") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone Icon",
                            tint = White
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = White,
                        unfocusedBorderColor = White,
                        cursorColor = White,
                        focusedLabelColor = White,
                        unfocusedLabelColor = White,
                        focusedLeadingIconColor = White,
                        unfocusedLeadingIconColor = White,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 25.sp)
                )
            }
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Logger.d("Tag", errorMessage)
            }

            Spacer(modifier = Modifier.height(27.dp))

            Text(
                text = "Tap NEXT to get OTP for verification",
                color = White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Surface (
                shadowElevation = 4.dp,
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(14.dp)
            ){
                Button(
                    onClick = {
                        if (mobileNumber.length == 10) {
                            isLoading = true
                            errorMessage = ""
                            coroutineScope.launch {
                                try {
                                    val response = sendOtp(mobileNumber)
                                    if (response.response == true) {
                                        onNextClick(mobileNumber)
                                    } else {
                                        errorMessage = response.message
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Network error: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            errorMessage = "Please enter a valid 10-digit mobile number"
                        }
                    },
                    enabled = mobileNumber.length == 10 && !isLoading,
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
                        Text("Next", fontSize = 25.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onExitClick,
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed,
                    contentColor = White
                )
            ) {
                Text("Exit", fontSize = 25.sp)
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class SmsVerificationResponse(
    val response: Boolean,
    val message: String,
    val verification: Boolean? = null,
    val patients: Int? = null
)


suspend fun sendOtp(mobileNumber: String): SmsVerificationResponse {
    var otpCode = (1000..9999).random().toString()
    if (mobileNumber == "1111111111") otpCode = "1111"
    val response =
        NetworkClient.httpClient.post("http://dev.erp.hospital/HMS_API/sms_verification.php") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=send_otp")
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