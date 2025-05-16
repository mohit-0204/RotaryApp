package com.rotary.hospital

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
            composable<AppRoute.Home> {
                HomeScreen()
            }

            composable<AppRoute.Login> {
                LoginScreen(
                    onNextClick = { phoneNumber ->
                        navController.navigate(AppRoute.OtpVerification(phoneNumber))
                    },
                    onExitClick = { /* Platform-specific exit later */ }
                )
            }
            composable<AppRoute.PatientSelection> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.PatientSelection>()
                PatientListScreen(phoneNumber = route.phoneNumber, navController = navController)
            }
            composable<AppRoute.PatientRegistration> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.PatientRegistration>()
                RegistrationScreen(
                    onBack = {},
                    onSave = {},
                    onCancel = {})
            }


            composable<AppRoute.OtpVerification> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OtpVerification>()
                OtpVerificationScreen(
                    phoneNumber = route.phoneNumber,
                    onVerified = { patients ->
                        navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
//                        navController.navigate(AppRoute.PatientSelection(route.phoneNumber))
                    },
                    onResend = {

                    },
                    onBack = {

                    }
                )
            }
        }
    }
}