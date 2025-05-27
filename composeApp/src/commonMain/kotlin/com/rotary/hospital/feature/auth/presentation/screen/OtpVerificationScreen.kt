package com.rotary.hospital.feature.auth.presentation.screen

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
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.ErrorRed
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.feature.auth.domain.usecase.SendOtpUseCase
import com.rotary.hospital.feature.auth.presentation.viewmodel.OtpVerificationState
import com.rotary.hospital.feature.auth.presentation.viewmodel.OtpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.logo

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
        val focusRequesters = remember { List(4) { FocusRequester() } }
        val focusManager = LocalFocusManager.current
        val keyboardManager = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            viewModel.setMobileNumber(phoneNumber)
            focusRequesters[0].requestFocus()
        }
        LaunchedEffect(countdown) {
            if (countdown > 0) {
                while (countdown > 0) {
                    delay(1000)
                    countdown--
                }
                resendEnabled = true
            }
        }
        LaunchedEffect(state.focusedIndex) {
            state.focusedIndex?.let { index ->
                focusRequesters.getOrNull(index)?.requestFocus()
            }
        }
        LaunchedEffect(state.code, keyboardManager) {
            if (state.code.none { it == null }) {
                focusManager.clearFocus()
                keyboardManager?.hide()
            }
        }
        LaunchedEffect(otpState) {
            if (otpState is OtpVerificationState.Success) {
                val response = (otpState as OtpVerificationState.Success).response
                onVerified(response.patients ?: 0)
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
                OtpScreen(
                    state = state,
                    onAction = { action ->
                        when (action) {
                            is OtpAction.OnEnterNumber -> {
                                if (action.number != null && action.index < 3) {
                                    focusRequesters[action.index + 1].requestFocus()
                                }
                            }
                            else -> Unit
                        }
                        viewModel.onAction(action)
                    },
                    focusRequester = focusRequesters,
                    modifier = Modifier
                )
                if (otpState is OtpVerificationState.Error) {
                    Text(
                        text = (otpState as OtpVerificationState.Error).message,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        if (resendEnabled) {
                            coroutineScope.launch {
                                viewModel.onAction(OtpAction.ClearFields)
                                viewModel.resendOtp() // Call the new function
                                countdown = 40
                                resendEnabled = false
                                onResend()
                            }
                        }
                    },
                    enabled = resendEnabled && otpState !is OtpVerificationState.Loading
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
                    onClick = { viewModel.verifyOtp() },
                    enabled = state.code.all { it != null } && otpState !is OtpVerificationState.Loading,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp).height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimary,
                        contentColor = White,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.White
                    )
                ) {
                    if (otpState is OtpVerificationState.Loading) {
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
