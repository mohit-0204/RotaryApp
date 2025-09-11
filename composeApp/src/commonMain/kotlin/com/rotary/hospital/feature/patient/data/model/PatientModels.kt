package com.rotary.hospital.feature.patient.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientRegistrationResponse(
    val response: Boolean,
    val data: List<ApiPatient>? = null
)

@Serializable
data class ApiPatient(
    @SerialName("pid1") val id: String,
    @SerialName("p_name") val name: String
)

@Serializable
data class PatientListResponse(
    val response: Boolean,
    val message: String,
    val data: List<ApiPatient>
)

@Serializable
data class ApiPatientProfile(
    @SerialName("p_name") val name: String = "",
    @SerialName("p_father_husband") val guardianName: String = "",
    @SerialName("p_age") val age: String = "",
    @SerialName("p_addresss") val address: String = "",
    @SerialName("p_text6") val email: String = "",
    @SerialName("p_city") val city: String = "",
    @SerialName("p_state") val state: String = "",
    @SerialName("p_bloodtype") val bloodGroup: String = "",
    @SerialName("p_sex") val gender: String = ""
)

@Serializable
data class PatientProfileResponse(
    val response: Boolean,
    val data: List<ApiPatientProfile>
)