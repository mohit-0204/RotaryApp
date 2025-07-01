package com.rotary.hospital.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.rotary.hospital.core.ui.toastController
import com.rotary.hospital.feature.auth.presentation.screen.LoginScreen
import com.rotary.hospital.feature.auth.presentation.screen.OtpVerificationScreen
import com.rotary.hospital.feature.home.presentation.model.HomeAction
import com.rotary.hospital.feature.opd.presentation.screen.DoctorAvailabilityScreen
import com.rotary.hospital.feature.opd.presentation.screen.OpdPatientListScreen
import com.rotary.hospital.feature.opd.presentation.screen.OpdPaymentFailedScreen
import com.rotary.hospital.feature.opd.presentation.screen.OpdPaymentPendingScreen
import com.rotary.hospital.feature.opd.presentation.screen.OpdPaymentSuccessScreen
import com.rotary.hospital.feature.opd.presentation.screen.RegisterNewOpdScreen
import com.rotary.hospital.feature.opd.presentation.screen.RegisteredOpdsScreen
import com.rotary.hospital.feature.opd.presentation.screen.SelectedOpdDetailsScreen
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
        var mobileNumber by remember { mutableStateOf("")}
        // Global snackbar setup
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
//            val patientId = preferences.getString(PreferenceKeys.PATIENT_ID, "").first()
            mobileNumber = preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").first()
            Logger.d("App", "Mobile number loaded: $mobileNumber")
            toastController.bind(snackbarHostState, scope)

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
                HomeScreen(
                    route.patientName,
                    onItemClick = { action ->
                        when(action){
                            HomeAction.BookOPD -> {
                                if (mobileNumber.isNotBlank()) {
                                    navController.navigate(AppRoute.RegisteredOpds(mobileNumber))
                                } else {
                                    Logger.e("HomeScreen", "Mobile number not available")
                                }
                            }
                            HomeAction.ContactUs -> {
                                // todo yet to be implemented
                            }
                            HomeAction.ManageMedicineReminders -> {
                                // todo yet to be implemented
                            }
                            HomeAction.OpenSettings -> {
                                // todo yet to be implemented
                            }
                            HomeAction.ViewLabTests -> {
                                // todo yet to be implemented
                            }
                            HomeAction.ViewPatientProfile -> {
                                // todo yet to be implemented
                            }
                            HomeAction.ViewTerms -> {
                                // todo yet to be implemented
                            }
                        }

                    },
                    snackbarHostState = snackbarHostState

                )
            }
            composable<AppRoute.Login> {
                LoginScreen(
                    onNextClick = { phoneNumber ->
                        navController.navigate(AppRoute.OtpVerification(phoneNumber))
                    },
                    onExitClick = {
                        // Platform-specific exit
                    }
                )
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
                            navController.navigate(AppRoute.ProfilePatientSelection(route.phoneNumber))
                        else
                            navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
                    },
                    onResend = {
                        //Resend OTP logic
                    },
                    onBack = {
                        Logger.d("TAG", "Navigating back from OtpVerification to Login")
                        navController.popBackStack<AppRoute.Login>(inclusive = false)
                    }
                )
            }

            composable<AppRoute.RegisteredOpds> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.RegisteredOpds>()
                RegisteredOpdsScreen(
                    onOpdClick = { opdId ->
                        navController.navigate(AppRoute.SelectedOpdDetails(route.mobileNumber, opdId))
                    },
                    onAddNew = {
                        navController.navigate(AppRoute.OpdPatientList(route.mobileNumber))
                    },
                    onBackClick = {navController.popBackStack()},
                    onSearchClick = {}
                )
            }

            composable<AppRoute.ProfilePatientSelection> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.ProfilePatientSelection>()
                PatientListScreen(
                    phoneNumber = route.phoneNumber,
                    onAddPatient = {
                        navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
                    },
                    onPatientSelected = { patientName ->
                        navController.navigate(AppRoute.Home(patientName)) {
                            popUpTo<AppRoute.ProfilePatientSelection> { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() })
            }
            composable<AppRoute.OpdPatientList> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OpdPatientList>()
                OpdPatientListScreen(
                    onPatientClick = { patientId, patientName ->
                        navController.navigate(AppRoute.RegisterNewOpd(route.mobileNumber, patientId, patientName))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<AppRoute.RegisterNewOpd> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.RegisterNewOpd>()
                RegisterNewOpdScreen(
                    onPaymentInitiated = { payment ->
                        // TODO: Handle PhonePe payment initiation
                        navController.navigate(AppRoute.OpdPaymentPending("Processing payment..."))
                    },
                    onSuccess = { response ->
//                        navController.navigate(AppRoute.OpdPaymentSuccess(response.transactionId))
                    },
                    onBack = { navController.popBackStack() },
                    patientId = route.patientId,
                    patientName = route.patientName,
                    mobileNumber = route.mobileNumber
                )
            }
            composable<AppRoute.DoctorAvailability> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.DoctorAvailability>()
                DoctorAvailabilityScreen(
                    doctorId = route.doctorId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<AppRoute.OpdPaymentSuccess> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OpdPaymentSuccess>()
                OpdPaymentSuccessScreen(
                    merchantTransactionId = route.merchantTransactionId,
                    onShareScreenshot = {
                        // TODO: Implement platform-specific screenshot sharing
                    },
                    onBack = {
                        navController.navigate(AppRoute.RegisteredOpds(mobileNumber)) {
                            popUpTo<AppRoute.OpdPaymentSuccess> { inclusive = true }
                        }
                    }
                )
            }
            composable<AppRoute.OpdPaymentPending> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OpdPaymentPending>()
                OpdPaymentPendingScreen(
                    message = route.message,
                    onRetry = {
                        // TODO: Retry payment logic
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<AppRoute.OpdPaymentFailed> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OpdPaymentFailed>()
                OpdPaymentFailedScreen(
                    message = route.message,
                    onRetry = {
                        // TODO: Retry payment logic
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<AppRoute.SelectedOpdDetails> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.SelectedOpdDetails>()
                SelectedOpdDetailsScreen(
                    opdId = route.opdId,
                    onBack = { navController.popBackStack() }
                )
            }
        }

    }
}