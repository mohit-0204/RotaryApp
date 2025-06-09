package com.rotary.hospital.feature.opd.data.remote

import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.network.ApiConstants
import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.feature.opd.data.remote.dto.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class OpdService {
    private val client = NetworkClient.httpClient
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getBookedOpds(mobileNumber: String): Result<List<OpdDto>> {
        return try {
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
            Logger.d("OpdService", "getBookedOpds: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList = json.decodeFromString<List<OpdDto>>(jsonObject["data"].toString())
                Result.success(dtoList)
            } else {
                Result.failure(Exception(jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getBookedOpds error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getRegisteredPatients(mobileNumber: String): Result<List<PatientDto>> {
        return try {
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
            Logger.d("OpdService", "getRegisteredPatients: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList = json.decodeFromString<List<PatientDto>>(jsonObject["data"]?.jsonArray.toString())
                Result.success(dtoList)
            } else {
                Result.failure(Exception(jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getRegisteredPatients error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getSpecializations(): Result<List<SpecializationDto>> {
        return try {
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
            Logger.d("OpdService", "getSpecializations: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList = json.decodeFromString<List<SpecializationDto>>(jsonObject["data"]?.jsonArray.toString())
                Result.success(dtoList)
            } else {
                Result.failure(Exception(jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getSpecializations error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDoctors(specialization: String): Result<List<DoctorDto>> {
        return try {
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
            Logger.d("OpdService", "getDoctors: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList = json.decodeFromString<List<DoctorDto>>(jsonObject["data"]?.jsonArray.toString())
                Result.success(dtoList)
            } else {
                Result.failure(Exception(jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getDoctors error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getSlots(doctorId: String): Result<List<SlotDto>> {
        return try {
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
            Logger.d("OpdService", "getSlots: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList = json.decodeFromString<List<SlotDto>>(jsonObject["data"]?.jsonArray.toString())
                Result.success(dtoList)
            } else {
                Result.failure(Exception(jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getSlots error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAvailability(doctorId: String, slotId: String): Result<AvailabilityDto?> {
        return try {
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
            Logger.d("OpdService", "getAvailability: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList = json.decodeFromString<List<AvailabilityDto>>(jsonObject["data"]?.jsonArray.toString())
                Result.success(dtoList.firstOrNull())
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getAvailability error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDoctorAvailability(doctorId: String): Result<Pair<List<DoctorAvailabilityDto>, List<LeaveDto>>> {
        return try {
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
            Logger.d("OpdService", "getDoctorAvailability: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val availability = json.decodeFromString<List<DoctorAvailabilityDto>>(jsonObject["data"]?.jsonArray.toString())
                val leaves = json.decodeFromString<List<LeaveDto>>(jsonObject["on_leave"]?.jsonArray.toString())
                Result.success(availability to leaves)
            } else {
                Result.success(emptyList<DoctorAvailabilityDto>() to emptyList<LeaveDto>())
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getDoctorAvailability error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getPaymentReference(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ): Result<PaymentRequestDto?> {
        return try {
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
            Logger.d("OpdService", "getPaymentReference: $responseBody")
            val dto = json.decodeFromString<PaymentRequestDto>(responseBody)
            Result.success(dto)
        } catch (e: Exception) {
            Logger.e("OpdService", "getPaymentReference error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getPaymentStatus(merchantTransactionId: String): Result<PaymentStatusDto> {
        return try {
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
            Logger.d("OpdService", "getPaymentStatus: $responseBody")
            val dto = json.decodeFromString<PaymentStatusDto>(responseBody)
            Result.success(dto)
        } catch (e: Exception) {
            Logger.e("OpdService", "getPaymentStatus error: ${e.message}")
            Result.failure(e)
        }
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
    ): Result<InsertOpdResponseDto> {
        return try {
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
            Logger.d("OpdService", "insertOpd: $responseBody")
            val dto = json.decodeFromString<InsertOpdResponseDto>(responseBody)
            Result.success(dto)
        } catch (e: Exception) {
            Logger.e("OpdService", "insertOpd error: ${e.message}")
            Result.failure(e)
        }
    }
}