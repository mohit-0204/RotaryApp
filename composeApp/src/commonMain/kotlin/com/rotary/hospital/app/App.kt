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
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.core.ui.toastController
import com.rotary.hospital.feature.auth.presentation.screen.LoginScreen
import com.rotary.hospital.feature.auth.presentation.screen.OtpVerificationScreen
import com.rotary.hospital.feature.home.presentation.model.HomeAction
import com.rotary.hospital.feature.home.presentation.screen.ContactUsScreen
import com.rotary.hospital.feature.home.presentation.screen.TermsScreen
import com.rotary.hospital.feature.opd.presentation.model.TransactionDetails
import com.rotary.hospital.feature.opd.presentation.screen.DoctorAvailabilityScreen
import com.rotary.hospital.feature.opd.presentation.screen.OpdPatientListScreen
import com.rotary.hospital.feature.opd.presentation.screen.OpdPatientRegistrationScreen
import com.rotary.hospital.feature.opd.presentation.screen.OpdPaymentResultScreen
import com.rotary.hospital.feature.opd.presentation.screen.RegisterNewOpdScreen
import com.rotary.hospital.feature.opd.presentation.screen.RegisteredOPDsScreen
import com.rotary.hospital.feature.opd.presentation.screen.SelectedOpdDetailsScreen
import com.rotary.hospital.feature.patient.presentation.screen.PatientListScreen
import com.rotary.hospital.feature.patient.presentation.screen.PatientProfileScreen
import com.rotary.hospital.feature.patient.presentation.screen.RegistrationScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject


@Composable
@Preview
fun App(paymentHandler: PaymentHandler) {
    AppTheme {
        val preferences: PreferencesManager = koinInject()
        val navController = rememberNavController()
        var mobileNumber by remember { mutableStateOf("") }
        var patientName by remember { mutableStateOf("") }
        var patientId by remember { mutableStateOf("") }
        var isLoggedIn by remember { mutableStateOf(false) }
        // Global snackbar setup
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            isLoggedIn = preferences.getBoolean(PreferenceKeys.IS_LOGGED_IN, false).first()
            patientName = preferences.getString(PreferenceKeys.PATIENT_NAME, "").first()
            mobileNumber = preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").first()
            patientId = preferences.getString(PreferenceKeys.PATIENT_ID, "").first()

            Logger.d("App", "Mobile number loaded: $mobileNumber")
            Logger.d("App", "Patient name loaded: $patientName")
            Logger.d("App", "Patient id loaded: $patientId")
            toastController.bind(snackbarHostState, scope)

        }

        NavHost(
            navController = navController,
            startDestination = AppRoute.Splash,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<AppRoute.Splash> {
                SplashScreen {
                    if (isLoggedIn) {
                        navController.navigate(AppRoute.Home(patientName)) {
                            popUpTo<AppRoute.Splash> { inclusive = true }
                        }
                    } else navController.navigate(AppRoute.Login) {
                        popUpTo<AppRoute.Splash> { inclusive = true }
                    }
                }
            }
            composable<AppRoute.Home> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.Home>()
                HomeScreen(
                    route.patientName, onLogout = {
                    navController.navigate(AppRoute.Login) {
                        popUpTo<AppRoute.Home> { inclusive = true }
                    }
                }, onItemClick = { action ->
                    scope.launch {
                        val currentMobile = mobileNumber.ifBlank {
                            preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").first()
                        }
                        when (action) {
                            HomeAction.BookOPD -> {
                                if (currentMobile.isNotBlank()) {
                                    navController.navigate(AppRoute.RegisteredOpds(currentMobile))
                                } else {
                                    Logger.e("HomeScreen", "Mobile number not available")
                                }
                            }

                            HomeAction.ContactUs -> {
                                navController.navigate(AppRoute.ContactUs)
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
                                navController.navigate(AppRoute.PatientProfile)
                            }

                            HomeAction.ViewTerms -> {
                                navController.navigate(AppRoute.TermsAndConditions)
                            }
                        }
                    }

                }, snackbarHostState = snackbarHostState

                )
            }
            composable<AppRoute.Login> {
                LoginScreen(onNextClick = { phoneNumber ->
                    navController.navigate(AppRoute.OtpVerification(phoneNumber))
                }, onExitClick = {
                    // Platform-specific exit
                })
            }
            composable<AppRoute.ContactUs> {
                ContactUsScreen(
                    onBack = {
                        navController.popBackStack()
                    })
            }
            composable<AppRoute.TermsAndConditions> {
                TermsScreen(
                    onBack = {
                        navController.popBackStack()
                    })
            }

            composable<AppRoute.PatientRegistration> { backStackEntry ->
                RegistrationScreen(
                    onBack = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() },
                    onSave = { patientName ->
                        navController.navigate(AppRoute.Home(patientName)) {
                            popUpTo<AppRoute.PatientRegistration> { inclusive = true }
                        }
                    })
            }


            composable<AppRoute.OpdPatientRegistration> { backStackEntry ->
                OpdPatientRegistrationScreen(
                    onBack = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() },
                    onSave = { patientId, patientName ->
                        navController.navigate(
                            AppRoute.RegisterNewOpd(
                                mobileNumber, patientId, patientName
                            )
                        )
                    })
            }

            composable<AppRoute.PatientProfile> { backStackEntry ->
                PatientProfileScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { patientName ->
                        navController.navigate(AppRoute.Home(patientName)) {
                            popUpTo<AppRoute.PatientProfile> { inclusive = true }
                        }
                    })
            }
            composable<AppRoute.OtpVerification> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OtpVerification>()
                OtpVerificationScreen(
                    phoneNumber = route.phoneNumber,
                    onVerified = { patientCount ->
                        Logger.d("TAG", "PatientCount: $patientCount")
                        if (patientCount > 0) navController.navigate(
                            AppRoute.ProfilePatientSelection(
                                route.phoneNumber
                            )
                        )
                        else navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
                    },
                    onResend = {
                        //Resend OTP logic
                    },
                    onBack = {
                        Logger.d("TAG", "Navigating back from OtpVerification to Login")
                        navController.popBackStack<AppRoute.Login>(inclusive = false)
                    })
            }

            composable<AppRoute.RegisteredOpds> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.RegisteredOpds>()
                RegisteredOPDsScreen(onOpdClick = { opdId ->
                    navController.navigate(
                        AppRoute.SelectedOpdDetails(
                            route.mobileNumber, opdId
                        )
                    )
                }, onAddNew = {
                    navController.navigate(AppRoute.OpdPatientList(route.mobileNumber))
                }, onBackClick = { navController.popBackStack() }, onSearchClick = {})
            }

            composable<AppRoute.ProfilePatientSelection> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.ProfilePatientSelection>()
                PatientListScreen(phoneNumber = route.phoneNumber, onAddPatient = {
                    navController.navigate(AppRoute.PatientRegistration(route.phoneNumber))
                }, onPatientSelected = { patientName ->
                    navController.navigate(AppRoute.Home(patientName)) {
                        popUpTo<AppRoute.ProfilePatientSelection> { inclusive = true }
                    }
                }, onBackClick = { navController.popBackStack() })
            }
            composable<AppRoute.OpdPatientList> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OpdPatientList>()
                OpdPatientListScreen(onPatientClick = { patientId, patientName ->
                    navController.navigate(
                        AppRoute.RegisterNewOpd(
                            route.mobileNumber, patientId, patientName
                        )
                    )
                }, onAddPatient = {
                    navController.navigate(AppRoute.OpdPatientRegistration(route.mobileNumber))
                }, onBack = { navController.popBackStack() })
            }
            composable<AppRoute.DoctorAvailability> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.DoctorAvailability>()
                DoctorAvailabilityScreen(
                    doctorId = route.doctorId, onBack = { navController.popBackStack() })
            }
            composable<AppRoute.RegisterNewOpd> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.RegisterNewOpd>()
                RegisterNewOpdScreen(
                    paymentHandler = paymentHandler,
                    onPaymentResult = { transactionDetails: TransactionDetails ->
                        val transactionDetailsJson = Json.encodeToString(transactionDetails)
                        val encodedTransactionDetailsJson =
                            transactionDetailsJson.encodeUtf8().base64()
                        navController.navigate(
                            AppRoute.OpdPaymentResult(
                                encodedTransactionDetailsJson
                            )
                        )
                    },
                    onBack = { navController.popBackStack() },
                    patientId = route.patientId,
                    patientName = route.patientName,
                    mobileNumber = route.mobileNumber,
                    snackbarHostState = snackbarHostState

                )
            }

            composable<AppRoute.OpdPaymentResult> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.OpdPaymentResult>()
                val encodedTransactionDetails = route.transactionDetails
                val transactionDetailsJson = encodedTransactionDetails.decodeBase64()?.utf8()
                val transactionDetails =
                    Json.decodeFromString<TransactionDetails>(transactionDetailsJson!!)
                OpdPaymentResultScreen(
                    transactionDetails = transactionDetails,
                    onShareScreenshot = {
                        // TODO: Implement platform-specific screenshot sharing
                    },
                    onBack = {
                        navController.navigate(AppRoute.RegisteredOpds(mobileNumber)) {
                            popUpTo<AppRoute.OpdPaymentResult> { inclusive = true }
                        }
                    })
            }
            composable<AppRoute.SelectedOpdDetails> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRoute.SelectedOpdDetails>()
                SelectedOpdDetailsScreen(
                    opdId = route.opdId,
                    onBack = { navController.popBackStack() },
                    onShare = { })
            }
        }

    }
}