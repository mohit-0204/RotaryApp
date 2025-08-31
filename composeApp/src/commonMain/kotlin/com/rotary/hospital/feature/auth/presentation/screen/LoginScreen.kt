package com.rotary.hospital.feature.auth.presentation.screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.feature.auth.presentation.viewmodel.LoginState
import com.rotary.hospital.feature.auth.presentation.viewmodel.LoginViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.logo
import rotaryhospital.composeapp.generated.resources.welcome_to


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
                text = stringResource(Res.string.welcome_to),
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
    onExitClick: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val mobileNumber by viewModel.mobileNumber.collectAsState()


    if (loginState is LoginState.Error) {
        Logger.d("LoginScreen",(loginState as LoginState.Error).message)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = (loginState as LoginState.Error).message,
            color = MaterialTheme.colorScheme.error,
            fontSize = 14.sp
        )
    }
    if (loginState is LoginState.Success) {

        LaunchedEffect(Unit) {
            onNextClick(mobileNumber)
            viewModel.resetLoginState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
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
                            viewModel.setMobileNumber(it)
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
            ElevatedButton(
                onClick = { viewModel.sendOtp() },
                enabled = mobileNumber.length == 10 && loginState !is LoginState.Loading,
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
                when (loginState) {
                    is LoginState.Loading -> {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    }
                    else -> {
                        Text("NEXT", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedButton(
                onClick = onExitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .padding(bottom = 10.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("EXIT", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }


        }
    }
}
