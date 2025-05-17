package com.rotary.hospital

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rotary.hospital.di.appModule
import org.koin.compose.KoinApplication
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        KoinApplication(application = {
            modules(appModule)
        }) {
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
                        onExitClick = { /* Platform-specific exit */ }
                    )
                }
                composable<AppRoute.PatientSelection> { backStackEntry ->
                    val route = backStackEntry.toRoute<AppRoute.PatientSelection>()
                    PatientListScreen(phoneNumber = route.phoneNumber, navController = navController)
                }
                composable<AppRoute.PatientRegistration> { backStackEntry ->
                    val route = backStackEntry.toRoute<AppRoute.PatientRegistration>()
                    RegistrationScreen(
                        onBack = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                        onSave = {
                            navController.navigate(AppRoute.PatientSelection(route.phoneNumber)) {
                                popUpTo<AppRoute.PatientRegistration> { inclusive = true }
                            }
                        }
                    )
                }
                composable<AppRoute.OtpVerification> { backStackEntry ->
                    val route = backStackEntry.toRoute<AppRoute.OtpVerification>()
                    OtpVerificationScreen(
                        phoneNumber = route.phoneNumber,
                        onVerified = {
                            navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
                        },
                        onResend = { /* Resend OTP logic */ },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}