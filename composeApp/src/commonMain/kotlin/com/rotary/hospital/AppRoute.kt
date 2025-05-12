package com.rotary.hospital

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
    data object Home : AppRoute()
}

@Serializable
data class Patient(val id: String, val name: String, val phoneNumber: String)