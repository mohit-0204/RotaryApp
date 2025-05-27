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
    data class PatientSelection(val phoneNumber: String) : AppRoute()

    @Serializable
    data class PatientRegistration(val phoneNumber: String) : AppRoute()

    @Serializable
    data class Home(val patientName: String, val patientId: String) : AppRoute()
}

