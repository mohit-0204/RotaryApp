package com.rotary.hospital.feature.opd.data.network

import com.rotary.hospital.core.network.ApiConstants
import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.feature.opd.data.model.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class OpdService {
    private val client = NetworkClient.httpClient
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getBookedOpds(mobileNumber: String): List<Opd> {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=get_booked_app")
                    append("&mobile_number=$mobileNumber")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        val jsonObject = json.decodeFromString<JsonObject>(responseBody)
        return if (jsonObject["response"]?.jsonPrimitive?.boolean == true && jsonObject["message"]?.jsonPrimitive?.content == "OK") {
            json.decodeFromString<List<Opd>>(jsonObject["data"]?.jsonArray.toString())
        } else {
            emptyList()
        }
    }

    suspend fun getRegisteredPatients(mobileNumber: String): List<Patient> {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.PATIENT_DATA) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=get_registered_patients")
                    append("&mobile_number=$mobileNumber")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        val jsonObject = json.decodeFromString<JsonObject>(responseBody)
        return if (jsonObject["response"]?.jsonPrimitive?.boolean == true && jsonObject["message"]?.jsonPrimitive?.content == "OK") {
            json.decodeFromString<List<Patient>>(jsonObject["data"]?.jsonArray.toString())
        } else {
            emptyList()
        }
    }

    suspend fun getSpecializations(): List<Specialization> {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=get_doctor_specilizations")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        val jsonObject = json.decodeFromString<JsonObject>(responseBody)
        return if (jsonObject["response"]?.jsonPrimitive?.boolean == true && jsonObject["message"]?.jsonPrimitive?.content == "OK") {
            json.decodeFromString<List<Specialization>>(jsonObject["data"]?.jsonArray.toString())
        } else {
            emptyList()
        }
    }

    suspend fun getDoctors(specialization: String): List<Doctor> {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=get_doctor")
                    append("&spec=$specialization")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        val jsonObject = json.decodeFromString<JsonObject>(responseBody)
        return if (jsonObject["response"]?.jsonPrimitive?.boolean == true && jsonObject["message"]?.jsonPrimitive?.content == "OK") {
            json.decodeFromString<List<Doctor>>(jsonObject["data"]?.jsonArray.toString())
        } else {
            emptyList()
        }
    }

    suspend fun getSlots(doctorId: String): List<Slot> {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=get_slots")
                    append("&doc_id=$doctorId")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        val jsonObject = json.decodeFromString<JsonObject>(responseBody)
        return if (jsonObject["response"]?.jsonPrimitive?.boolean == true && jsonObject["message"]?.jsonPrimitive?.content == "OK") {
            json.decodeFromString<List<Slot>>(jsonObject["data"]?.jsonArray.toString())
        } else {
            emptyList()
        }
    }

    suspend fun getAvailability(doctorId: String, slotId: String): Availability? {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=get_availability_new")
                    append("&doc_id=$doctorId")
                    append("&slot_id=$slotId")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        val jsonObject = json.decodeFromString<JsonObject>(responseBody)
        return if (jsonObject["response"]?.jsonPrimitive?.boolean == true && jsonObject["message"]?.jsonPrimitive?.content == "OK") {
            json.decodeFromString<List<Availability>>(jsonObject["data"]?.jsonArray.toString()).firstOrNull()
        } else {
            null
        }
    }

    suspend fun getDoctorAvailability(doctorId: String): Pair<List<DoctorAvailability>, List<Leave>> {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=check_availability")
                    append("&doc_id=$doctorId")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        val jsonObject = json.decodeFromString<JsonObject>(responseBody)
        return if (jsonObject["response"]?.jsonPrimitive?.boolean == true && jsonObject["message"]?.jsonPrimitive?.content == "OK") {
            val availability = json.decodeFromString<List<DoctorAvailability>>(jsonObject["data"]?.jsonArray.toString())
            val leaves = json.decodeFromString<List<Leave>>(jsonObject["on_leave"]?.jsonArray.toString())
            availability to leaves
        } else {
            emptyList<DoctorAvailability>() to emptyList<Leave>()
        }
    }

    suspend fun getPaymentReference(mobileNumber: String, amount: String, patientId: String, patientName: String, doctorName: String): PaymentRequest? {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.PAYMENT_API) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=payment_api_payPage")
                    append("&mobile_no=$mobileNumber")
                    append("&amount=$amount")
                    append("&p_id=$patientId")
                    append("&name=$patientName")
                    append("&doc_name=$doctorName")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        return json.decodeFromString<PaymentRequest>(responseBody)
    }

    suspend fun getPaymentStatus(merchantTransactionId: String): PaymentStatus {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.PAYMENT_API) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=payment_status")
                    append("&merchantTransactionId=$merchantTransactionId")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        return json.decodeFromString<PaymentStatus>(responseBody)
    }

    suspend fun insertOpd(
        patientId: String,
        patientName: String,
        mobileNumber: String,
        doctorName: String,
        doctorId: String,
        opdAmount: String,
        durationPerPatient: String,
        docTimeFrom: String,
        opdType: String,
        transactionId: String,
        paymentId: String,
        orderId: String,
        status: String,
        message: String
    ): InsertOpdResponse {
        val response = client.post(ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=insert_opd")
                    append("&p_id=$patientId")
                    append("&p_name=$patientName")
                    append("&mobile_number=$mobileNumber")
                    append("&doc_name=$doctorName")
                    append("&doc_id=$doctorId")
                    append("&opd_amount=$opdAmount")
                    append("&duration_per_patient=$durationPerPatient")
                    append("&doc_time_from=$docTimeFrom")
                    append("&opd_type=$opdType")
                    append("&transaction_id=$transactionId")
                    append("&payment_id=$paymentId")
                    append("&order_id=$orderId")
                    append("&status=$status")
                    append("&message=$message")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("OpdService", responseBody)
        return json.decodeFromString<InsertOpdResponse>(responseBody)
    }
}