package com.rotary.hospital.feature.opd.data.remote

import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.network.ApiConstants
import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.feature.opd.data.remote.dto.InsertOpdResponseDto
import com.rotary.hospital.feature.opd.data.remote.dto.PaymentRequestDto
import com.rotary.hospital.feature.opd.data.remote.dto.PaymentStatusDto
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

class PaymentService {
    private val client = NetworkClient.httpClient
    private val json = Json { ignoreUnknownKeys = true }

    private fun buildFormBody(params: Map<String, String>): String {
        return params.entries.joinToString("&") { (key, value) ->
            "$key=$value"
        }
    }

    suspend fun getPaymentReference(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ): Result<PaymentRequestDto> {
        return try {
            val response = client.post(ApiConstants.BASE_URL + ApiConstants.PAYMENT_API) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        mapOf(
                            "action" to "payment_api_payPage",
                            "mobile_no" to mobileNumber,
                            "amount" to amount,
                            "p_id" to patientId,
                            "name" to patientName,
                            "doc_name" to doctorName
                        )
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("PaymentService", "getPaymentReference: $responseBody")
            val dto = json.decodeFromString<PaymentRequestDto>(responseBody)
            Result.success(dto)
        } catch (e: Exception) {
            Logger.e("PaymentService", "getPaymentReference error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getPaymentStatus(
        merchantTransactionId: String,
        doctorName: String, doctorId: String, docTime: String,
        durationPerPatient: String, opdType: String, orderId: String
    ): Result<PaymentStatusDto> {
        return try {
            val response = client.post(ApiConstants.BASE_URL + ApiConstants.PAYMENT_API) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        mapOf(
                            "action" to "new_payment_status",
                            "merchantTransactionId" to merchantTransactionId,
                            "doc_name" to doctorName,
                            "doc_id" to doctorId,
                            "doc_time_from" to docTime,
                            "duration_per_patient" to durationPerPatient,
                            "opd_type" to opdType,
                            "order_id" to orderId
                        )
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("PaymentService", "getPaymentStatus: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            val responseSuccess = jsonObject["response"]?.jsonPrimitive?.boolean ?: false
            val message = jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
            if (responseSuccess) {
                val dto = json.decodeFromString<PaymentStatusDto>(responseBody)
                Result.success(dto)
            } else {
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Logger.e("PaymentService", "getPaymentStatus error: ${e.message}")
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
                    buildFormBody(
                        mapOf(
                            "action" to "insert_opd",
                            "p_id" to patientId,
                            "p_name" to patientName,
                            "mobile_number" to mobileNumber,
                            "doc_name" to doctorName,
                            "doc_id" to doctorId,
                            "opd_amount" to opdAmount,
                            "duration_per_patient" to durationPerPatient,
                            "doc_time_from" to docTimeFrom,
                            "opd_type" to opdType,
                            "transaction_id" to transactionId,
                            "payment_id" to paymentId,
                            "order_id" to orderId,
                            "status" to status,
                            "message" to message,
                            "close" to "close"
                        )
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("PaymentService", "insertOpd: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            val responseSuccess = jsonObject["response"]?.jsonPrimitive?.boolean ?: false
            val message = jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
            if (responseSuccess && message == "success") {
                val dto = json.decodeFromString<InsertOpdResponseDto>(responseBody)
                Result.success(dto)
            } else {
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Logger.e("PaymentService", "insertOpd error: ${e.message}")
            Result.failure(e)
        }
    }
}