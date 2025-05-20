package com.rotary.hospital

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rotary.hospital.network.NetworkClient
import com.rotary.hospital.utils.Logger
import com.rotary.hospital.viewmodels.OtpViewModel
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
    phoneNumber: String, onVerified: (Int) -> Unit, onResend: () -> Unit, onBack: () -> Unit
) {
    AppTheme {
        val coroutineScope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        var resendEnabled by remember { mutableStateOf(false) }
        var countdown by remember { mutableStateOf(40) }

        LaunchedEffect(countdown) {
            if (countdown > 0) {
                while (countdown > 0) {
                    delay(1000)
                    countdown--
                }
                resendEnabled = true
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo",
                    colorFilter = ColorFilter.tint(ColorPrimary),
                    modifier = Modifier.width(200.dp).height(200.dp).padding(10.dp)
                )

                Text(
                    text = "OTP Verification",
                    color = ColorPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Enter the 4-digit OTP sent to +91 $phoneNumber",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                val viewModel = viewModel<OtpViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                val focusRequesterss = remember { List(4) { FocusRequester() } }
                val focusManager = LocalFocusManager.current
                val keyboardManager = LocalSoftwareKeyboardController.current

                LaunchedEffect(Unit) {
                    focusRequesterss[0].requestFocus() // Set initial focus to first field
                }
                LaunchedEffect(state.focusedIndex) {
                    state.focusedIndex?.let { index ->
                        focusRequesterss.getOrNull(index)?.requestFocus()
                    }
                }
                LaunchedEffect(state.code, keyboardManager) {
                    val allNumbersEntered = state.code.none { it == null }
                    if (allNumbersEntered) {
                        focusManager.clearFocus()
                        keyboardManager?.hide()
                    }
                }

                OtpScreen(
                    state = state,
                    onAction = { action ->
                        when (action) {
                            is OtpAction.OnEnterNumber -> {
                                if (action.number != null && action.index < 3) {
                                    focusRequesterss[action.index + 1].requestFocus()
                                }
                            }
                            else -> Unit
                        }
                        viewModel.onAction(action)
                    },
                    focusRequester = focusRequesterss,
                    modifier = Modifier
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                                        viewModel.onAction(OtpAction.ClearFields)
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
                    }, enabled = resendEnabled && !isLoading
                ) {
                    Text(
                        text = if (resendEnabled) "Resend OTP" else "Resend OTP in $countdown sec",
                        color = ColorPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val otp = state.code.joinToString("") { it?.toString() ?: "" }
                        if (otp.length == 4) {
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
                                    Logger.d("TAG", e.message.toString())
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            errorMessage = "Please enter a valid 4-digit OTP"
                        }
                    },
                    enabled = state.code.all { it != null } && !isLoading,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp).height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimary,
                        contentColor = White,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = White, modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Verify", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
                        .padding(bottom = 10.dp).height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed, contentColor = White
                    )
                ) {
                    Text("Back", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OtpInputField(
    number: Int?,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onNumberChanged: (Int?) -> Unit,
    onKeyBoardBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(number) {
        mutableStateOf(
            TextFieldValue(
                text = number?.toString().orEmpty(),
                selection = TextRange(
                    index = if (number != null) 1 else 0
                )
            )
        )
    }
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(White, shape = RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = if (isFocused) ColorPrimary else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                val newNumber = newText.text
                if (newNumber.length <= 1 && newNumber.isDigitsOnly()) {
                    onNumberChanged(newNumber.toIntOrNull())
                }
            },
            cursorBrush = SolidColor(ColorPrimary),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontSize = 36.sp,
                color = ColorPrimary
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .padding(10.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChanged(it.isFocused)
                }
                .onKeyEvent { event ->
                    if (event.key == Key.Backspace && event.type == KeyEventType.KeyUp) {
                        if (text.text.isEmpty()) {
                            onKeyBoardBack()
                        }
                        true
                    } else {
                        false
                    }
                },
            decorationBox = { innerBox ->
                innerBox()
                if (!isFocused && number == null) {
                    Text(
                        text = "-",
                        textAlign = TextAlign.Center,
                        color = ColorPrimary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.fillMaxSize().wrapContentSize()
                    )
                }
            }
        )
    }
}

fun String.isDigitsOnly(): Boolean = all { it.isDigit() }

data class OtpState(
    val code: List<Int?> = (1..4).map { null },
    val focusedIndex: Int? = 0, // Initialize focus on first field
    val isValid: Boolean? = null
)

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int) : OtpAction
    data class OnChangeFieldFocus(val index: Int) : OtpAction
    data object OnKeyBoardBack : OtpAction
    data object ClearFields : OtpAction
}

@Composable
fun OtpScreen(
    state: OtpState,
    focusRequester: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        state.code.forEachIndexed { index, number ->
            OtpInputField(
                number = number,
                focusRequester = focusRequester[index],
                onFocusChanged = { isFocused ->
                    if (isFocused) {
                        onAction(OtpAction.OnChangeFieldFocus(index))
                    }
                },
                onNumberChanged = { newNumber ->
                    onAction(OtpAction.OnEnterNumber(newNumber, index))
                },
                onKeyBoardBack = {
                    onAction(OtpAction.OnKeyBoardBack)
                },
                modifier = Modifier.size(56.dp)
            )
        }
    }
    state.isValid?.let { isValid ->
        Text(
            text = if (isValid) "OTP is Valid !" else "OTP is not valid !",
            color = if (isValid) ColorPrimary else ErrorRed,
            fontSize = 16.sp
        )
    }
}

suspend fun verifyOtp(mobileNumber: String, otpCode: String): SmsVerificationResponse {
    val response =
        NetworkClient.httpClient.post("http://rotaryapp.mdimembrane.com/HMS_API/sms_verification.php") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=verify_otp")
                    append("&mobile_number=$mobileNumber")
                    append("&otp_code=$otpCode")
                    append("&close=close")
                })
        }
    val responseBody = response.bodyAsText()
    Logger.d("TAG", responseBody)

    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString(responseBody)
}