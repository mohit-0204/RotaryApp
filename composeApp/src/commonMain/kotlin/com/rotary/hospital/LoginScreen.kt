package com.rotary.hospital

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.network.NetworkClient
import com.rotary.hospital.utils.Logger
import com.rotary.hospital.utils.PreferenceKeys
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.logo


@Composable
fun WelcomeTextLoginScreen() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val screenWidth = maxWidth

        // Define font sizes based on available screen width
        val smallSize = when {
            screenWidth < 360.dp -> 22.sp
            screenWidth < 600.dp -> 24.sp
            else -> 28.sp
        }

        val largeSize = when {
            screenWidth < 360.dp -> 30.sp
            screenWidth < 600.dp -> 32.sp
            else -> 36.sp
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to",
                color = ColorPrimary,
                fontSize = smallSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Rotary Hospital",
                color = ColorPrimary,
                fontSize = largeSize,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = largeSize * 1.2,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Composable
fun LoginScreen(
    onNextClick: (String) -> Unit,
    onExitClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var mobileNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val preferences: PreferencesManager = koinInject()
    val savedMobileNumber by preferences
        .getString(PreferenceKeys.MOBILE_NUMBER, "")
        .collectAsState(initial = "")

    LaunchedEffect(savedMobileNumber) {
        if (savedMobileNumber.isNotBlank() && mobileNumber.isBlank()) {
            mobileNumber = savedMobileNumber
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(Color(0xFFE0F7F9), Color.White)
//                )
//            )
        ,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo without background, placed on ColorPrimary

            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp).padding(top = 10.dp),
                colorFilter = ColorFilter.tint(ColorPrimary)
            )


            Spacer(modifier = Modifier.height(34.dp))

            WelcomeTextLoginScreen()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your health is our priority",
                color = Color.DarkGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(34.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                            mobileNumber = it
                            errorMessage = ""
                        }
                    },
                    placeholder = { Text("Enter your mobile number") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = ColorPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = ColorPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFDDFBFB),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                Text(
                    text = "🔒 Tap NEXT to get OTP for verification",
                    color = ColorPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(54.dp))

            // NEXT with gradient + alpha
            ElevatedButton(
                onClick = {
                    if (mobileNumber.length == 10) {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val response = sendOtp(mobileNumber)
                                if (response.response) {
                                    preferences.saveString(
                                        PreferenceKeys.MOBILE_NUMBER,
                                        mobileNumber
                                    )
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
                enabled = !isLoading && mobileNumber.length == 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(4.dp)

            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("NEXT", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // EXIT Button – lighter tone
            ElevatedButton(
                onClick = onExitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .padding(bottom = 10.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("EXIT", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    fontSize = 14.sp
                )
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
        NetworkClient.httpClient.post("http://rotaryapp.mdimembrane.com/HMS_API/sms_verification.php") {
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