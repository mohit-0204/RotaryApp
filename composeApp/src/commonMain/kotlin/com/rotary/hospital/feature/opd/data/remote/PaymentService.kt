package com.rotary.hospital.feature.opd.data.remote

import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.network.ApiConstants
import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.feature.opd.data.remote.dto.PaymentRequestDto
import com.rotary.hospital.feature.opd.data.remote.dto.PaymentStatusDto
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLParameter
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

    // Helper to build testable URL
    private fun buildLogUrl(base: String, params: Map<String, String>): String {
        val query = params.entries.joinToString("&") { (key, value) ->
            "${key.encodeURLParameter()}=${value.encodeURLParameter()}"
        }
        return "$base?$query"
    }

    suspend fun getPaymentReference(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String,
        doctorId: String,
        docTime: String,
        durationPerPatient: String,
        opdType: String,
        orderId: String
    ): Result<PaymentRequestDto> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.PAYMENT_API
            val params = mapOf(
                "action" to "payment_api_payPage",
                "mobile_no" to mobileNumber,
                "amount" to amount,
                "p_id" to patientId,
                "name" to patientName,
                "doc_name" to doctorName,
                "doc_id" to doctorId,
                "doc_time_from" to docTime,
                "duration_per_patient" to durationPerPatient,
                "opd_type" to opdType,
                "order_id" to orderId
            )
            Logger.d("PaymentService", "getPaymentReference url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(params)
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
            val url = ApiConstants.BASE_URL + ApiConstants.PAYMENT_API
            val params = mapOf(
                "action" to "new_payment_status",
                "merchantTransactionId" to merchantTransactionId,
                "doc_name" to doctorName,
                "doc_id" to doctorId,
                "doc_time_from" to docTime,
                "duration_per_patient" to durationPerPatient,
                "opd_type" to opdType,
                "order_id" to orderId
            )
            Logger.d("PaymentService", "getPaymentStatus url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("PaymentService", "getPaymentStatus: $responseBody")
            val dto = json.decodeFromString<PaymentStatusDto>(responseBody)
            Result.success(dto)

        } catch (e: Exception) {
            Logger.e("PaymentService", "getPaymentStatus error: ${e.message}")
            Result.failure(e)
        }
    }
}