package com.rotary.hospital.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoute {
    @Serializable
    data object Splash : AppRoute()

    @Serializable
    data object Login : AppRoute()

    @Serializable
    data class OtpVerification(val phoneNumber: String) : AppRoute()

    @Serializable
    data class ProfilePatientSelection(val phoneNumber: String) : AppRoute()
    @Serializable
    data class OpdPatientSelection(val phoneNumber: String) : AppRoute()

    @Serializable
    data class PatientRegistration(val phoneNumber: String) : AppRoute()

    @Serializable
    data class Home(val patientName: String) : AppRoute()

    @Serializable
    data class RegisteredOpds(val mobileNumber: String) : AppRoute()

    @Serializable
    data class OpdPatientList(val mobileNumber: String) : AppRoute()

    @Serializable
    data class RegisterNewOpd(
        val mobileNumber: String,
        val patientId: String,
        val patientName: String
    ) : AppRoute()

    @Serializable
    data class DoctorAvailability(val doctorId: String) : AppRoute()

    @Serializable
    data class OpdPaymentSuccess(val merchantTransactionId: String) : AppRoute()

    @Serializable
    data class OpdPaymentPending(val message: String) : AppRoute()

    @Serializable
    data class OpdPaymentFailed(val message: String) : AppRoute()

    @Serializable
    data class SelectedOpdDetails(val mobileNumber: String, val opdId: String) : AppRoute()
}