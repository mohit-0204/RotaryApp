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
    val response: String,
    val message: String,
    val data: List<ApiPatient>
)