package com.rotary.hospital

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rotary.hospital.utils.Logger
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {

        val navController = rememberNavController()

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
                HomeScreen(route.patientName, route.patientId)
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
                PatientListScreen(phoneNumber = route.phoneNumber, onAddPatient = {
                    navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
                },
                    onBackClick = { navController.popBackStack() })
            }
            composable<AppRoute.PatientRegistration> { backStackEntry ->
                RegistrationScreen(
                    onBack = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() },
                    onSave = { patientId, patientName ->
                        Logger.d("TAG", "PatientId: $patientId , PatientName: $patientName")
                        navController.navigate(AppRoute.Home(patientName, patientId)) {
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
                    onBack = { navController.popBackStack() }
                )
            }
        }

    }
}