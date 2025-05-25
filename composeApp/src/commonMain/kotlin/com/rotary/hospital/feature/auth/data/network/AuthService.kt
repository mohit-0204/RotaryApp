package com.rotary.hospital.feature.auth.data.network

import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.core.utils.Logger
import com.rotary.hospital.core.utils.PreferenceKeys
import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class AuthService {
    private val client = NetworkClient.httpClient
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sendOtp(mobileNumber: String): SmsVerificationResponse {
        var otpCode = (1000..9999).random().toString()
        if (mobileNumber == "1111111111") otpCode = "1111"
        val response = client.post(PreferenceKeys.BASE_URL + PreferenceKeys.SMS_VERIFICATION) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                buildString {
                    append("action=send_otp")
                    append("&mobile_number=$mobileNumber")
                    append("&otp_code=$otpCode")
                    append("&close=close")
                }
            )
        }
        val responseBody = response.bodyAsText()
        Logger.d("TAG", responseBody)
        return json.decodeFromString(responseBody)
    }

    suspend fun verifyOtp(mobileNumber: String, otpCode: String): SmsVerificationResponse {
        val response =
            client.post(PreferenceKeys.BASE_URL + PreferenceKeys.SMS_VERIFICATION) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildString {
                        append("action=verify_otp")
                        append("&mobile_number=$mobileNumber")
                        append("&otp_code=$otpCode")
                        append("&close=close")
                    }
                )
            }
        val responseBody = response.bodyAsText()
        Logger.d("TAG", responseBody)
        return json.decodeFromString(responseBody)
    }
}