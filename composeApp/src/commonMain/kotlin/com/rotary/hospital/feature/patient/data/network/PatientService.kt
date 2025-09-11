package com.rotary.hospital.feature.patient.data.network

import com.rotary.hospital.core.network.ApiConstants
import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.core.utils.safeApiCall
import com.rotary.hospital.feature.patient.data.model.PatientListResponse
import com.rotary.hospital.feature.patient.data.model.PatientProfileResponse
import com.rotary.hospital.feature.patient.data.model.PatientRegistrationResponse
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class PatientService {
    private val client = NetworkClient.httpClient
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

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
    ): Result<PatientRegistrationResponse> {
        return safeApiCall {
            val response = client.post(ApiConstants.BASE_URL + ApiConstants.PATIENT_DATA) {
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
                    })
            }
            val responseBody = response.bodyAsText()
            Logger.d("PatientService", responseBody)
            json.decodeFromString(responseBody)
        }
    }

    suspend fun getRegisteredPatients(mobileNumber: String): Result<PatientListResponse> {
        return safeApiCall {
            val response = client.get(ApiConstants.BASE_URL + ApiConstants.PATIENT_DATA) {
                parameter("action", "get_registered_patients")
                parameter("mobile_number", mobileNumber)
            }
            val responseBody = response.bodyAsText()
            Logger.d("PatientService", responseBody)
            json.decodeFromString(responseBody)
        }
    }

    suspend fun getPatientProfile(patientId: String): Result<PatientProfileResponse> {
        return safeApiCall {
            val response = client.get(ApiConstants.BASE_URL + ApiConstants.PATIENT_DATA) {
                parameter("action", "get_patient_profile")
                parameter("patient_id", patientId)
                parameter("close", "close")
            }
            val body = response.bodyAsText()
            Logger.d("PatientService", body)
            json.decodeFromString(body)
        }
    }

    suspend fun updatePatientProfile(
        mobileNumber: String,
        patientId: String,
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
    ): Result<PatientRegistrationResponse> {
        return safeApiCall {
            val response = client.post(ApiConstants.BASE_URL + ApiConstants.PATIENT_DATA) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildString {
                        append("action=update_patient")
                        append("&mobile_number=$mobileNumber")
                        append("&patient_id=$patientId")
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
                    })
            }
            val body = response.bodyAsText()
            Logger.d("PatientService", body)
            json.decodeFromString(body)
        }
    }
}