package com.rotary.hospital.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rotary.hospital.feature.home.presentation.screen.HomeScreen
import com.rotary.hospital.core.navigation.AppRoute
import com.rotary.hospital.core.theme.AppTheme
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.feature.auth.presentation.screen.LoginScreen
import com.rotary.hospital.feature.auth.presentation.screen.OtpVerificationScreen
import com.rotary.hospital.feature.patient.presentation.screen.PatientListScreen
import com.rotary.hospital.feature.patient.presentation.screen.RegistrationScreen
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    AppTheme {
        val preferences: PreferencesManager = koinInject()
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            val patientId = preferences.getString(PreferenceKeys.PATIENT_ID, "").first()
        }

        NavHost(
            navController = navController,
            startDestination = AppRoute.Splash,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<AppRoute.Splash> {
                SplashScreen {
                    navController.navigate(AppRoute.Login) {
                        popUpTo<AppRoute.Splash> { inclusive = true }
                    }
                }
            }
            composable<AppRoute.Home> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.Home>()
                HomeScreen(route.patientName)
            }
            composable<AppRoute.Login> {
                LoginScreen(
                    onNextClick = { phoneNumber ->
                        navController.navigate(AppRoute.OtpVerification(phoneNumber))
                    },
                    onExitClick = { /* Platform-specific exit */ }
                )
            }
            composable<AppRoute.PatientSelection> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.PatientSelection>()
                PatientListScreen(
                    phoneNumber = route.phoneNumber, onAddPatient = {
                        navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
                    },
                    onPatientSelected = { patientName ->
                        navController.navigate(AppRoute.Home(patientName)) {
                            popUpTo<AppRoute.PatientSelection> { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() })
            }
            composable<AppRoute.PatientRegistration> { backStackEntry ->
                RegistrationScreen(
                    onBack = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() },
                    onSave = { patientName ->
                        navController.navigate(AppRoute.Home(patientName)) {
                            popUpTo<AppRoute.PatientRegistration> { inclusive = true }
                        }
                    }
                )
            }
            composable<AppRoute.OtpVerification> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OtpVerification>()
                OtpVerificationScreen(
                    phoneNumber = route.phoneNumber,
                    onVerified = { patientCount ->
                        Logger.d("TAG", "PatientCount: $patientCount")
                        if (patientCount > 0)
                            navController.navigate(AppRoute.PatientSelection(route.phoneNumber))
                        else
                            navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
                    },
                    onResend = { /* Resend OTP logic */ },
                    onBack = {
                        Logger.d("TAG", "Navigating back from OtpVerification to Login")
                        navController.popBackStack<AppRoute.Login>(inclusive = false)
                    }
                )
            }
        }

    }
}