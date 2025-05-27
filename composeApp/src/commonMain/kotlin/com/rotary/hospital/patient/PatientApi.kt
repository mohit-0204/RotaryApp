package com.rotary.hospital.patient

import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.core.common.Logger
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PatientRegistrationResponse(
    val response: Boolean,
    val data: List<PatientData>? = null
)

@Serializable
data class PatientData(
    val pid1: String,
    val p_name: String
)

suspend fun registerPatient(
    mobileNumber: String,
    name: String,
    guardianType: String,
    guardianName: String,
    gender: String,
    age: String,
    bloodGroup: String,
    email: String,
    address: String,
    city: String,
    state: String
): PatientRegistrationResponse {
    val response = NetworkClient.httpClient.post("http://dev.erp.hospital/HMS_API/patient_data.php") {
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(
            buildString {
                append("action=insert_patient")
                append("&mobile_number=$mobileNumber")
                append("&patient_name=$name")
                append("&guardian_type=$guardianType")
                append("&guardian_name=$guardianName")
                append("&gender=$gender")
                append("&age=$age")
                append("&blood_group=$bloodGroup")
                append("&email_id=$email")
                append("&address=$address")
                append("&city=$city")
                append("&state=$state")
                append("&close=close")
            }
        )
    }
    val responseBody = response.bodyAsText()
    Logger.d("PatientApi", responseBody)

    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<PatientRegistrationResponse>(responseBody)
}