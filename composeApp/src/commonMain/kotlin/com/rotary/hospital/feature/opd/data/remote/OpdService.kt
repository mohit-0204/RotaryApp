package com.rotary.hospital.feature.opd.data.remote

import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.network.ApiConstants
import com.rotary.hospital.core.network.NetworkClient
import com.rotary.hospital.feature.opd.data.remote.dto.OpdDto
import com.rotary.hospital.feature.opd.data.remote.dto.PatientDto
import com.rotary.hospital.feature.opd.data.remote.dto.SpecializationDto
import com.rotary.hospital.feature.opd.data.remote.dto.DoctorDto
import com.rotary.hospital.feature.opd.data.remote.dto.SlotDto
import com.rotary.hospital.feature.opd.data.remote.dto.AvailabilityDto
import com.rotary.hospital.feature.opd.data.remote.dto.DoctorAvailabilityDto
import com.rotary.hospital.feature.opd.data.remote.dto.LeaveDto
import com.rotary.hospital.feature.opd.data.remote.dto.OpdDetailsDto
import com.rotary.hospital.feature.opd.domain.model.Opd
import com.rotary.hospital.feature.opd.domain.model.OpdDetails
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class OpdService {
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

    suspend fun getBookedOpds(mobileNumber: String): Result<List<OpdDto>> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS
            val params = mapOf(
                "action" to "get_booked_app",
                "mobile_number" to mobileNumber,
                "close" to "close"
            )
            Logger.d("OpdService", "getBookedOpds url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
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
                Result.failure(
                    Exception(
                        jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getBookedOpds error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getOpdDetails(opdId: String): Result<OpdDetailsDto?> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS
            val params = mapOf(
                "action" to "get_opd_detail",
                "opd_id" to opdId,
                "close" to "close"
            )
            Logger.d("OpdService", "getOpdDetails url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("OpdService", "getOpdDetails: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList =
                    json.decodeFromString<List<OpdDetailsDto>>(jsonObject["data"].toString())
                Result.success(dtoList.firstOrNull())
            } else {
                Result.failure(
                    Exception(
                        jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getOpdDetails error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getRegisteredPatients(mobileNumber: String): Result<List<PatientDto>> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.PATIENT_DATA
            val params = mapOf(
                "action" to "get_registered_patients",
                "mobile_number" to mobileNumber,
                "close" to "close"
            )
            Logger.d("OpdService", "getRegisteredPatients url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("OpdService", "getRegisteredPatients: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList =
                    json.decodeFromString<List<PatientDto>>(jsonObject["data"]?.jsonArray.toString())
                Result.success(dtoList)
            } else {
                Result.failure(
                    Exception(
                        jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getRegisteredPatients error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getSpecializations(): Result<List<SpecializationDto>> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS
            val params = mapOf(
                "action" to "get_doctor_specilizations",
                "close" to "close"
            )
            Logger.d("OpdService", "getSpecializations url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("OpdService", "getSpecializations: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList =
                    json.decodeFromString<List<SpecializationDto>>(jsonObject["data"].toString())
                Result.success(dtoList)
            } else {
                Result.failure(
                    Exception(
                        jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getSpecializations error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDoctors(specialization: String): Result<List<DoctorDto>> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS
            val params = mapOf(
                "action" to "get_doctor",
                "spec" to specialization,
                "close" to "close"
            )
            Logger.d("OpdService", "getDoctors url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("OpdService", "getDoctors: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList = json.decodeFromString<List<DoctorDto>>(jsonObject["data"].toString())
                Result.success(dtoList)
            } else {
                Result.failure(
                    Exception(
                        jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getDoctors error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getSlots(doctorId: String): Result<List<SlotDto>> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS
            val params = mapOf(
                "action" to "get_slots",
                "doc_id" to doctorId,
                "close" to "close"
            )
            Logger.d("OpdService", "getSlots url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("OpdService", "getSlots: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dtoList = json.decodeFromString<List<SlotDto>>(jsonObject["data"].toString())
                Result.success(dtoList)
            } else {
                Result.failure(
                    Exception(
                        jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getSlots error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAvailability(doctorId: String, slotId: String): Result<AvailabilityDto?> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS
            val params = mapOf(
                "action" to "get_availability_new",
                "doc_id" to doctorId,
                "slot_id" to slotId,
                "close" to "close"
            )
            Logger.d("OpdService", "getAvailability url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("OpdService", "getAvailability: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val dto = jsonObject["data"]?.let {
                    val dtoList = json.decodeFromString<List<AvailabilityDto>>(it.toString())
                    dtoList.firstOrNull()
                }
                Result.success(dto)
            } else {
                Result.failure(
                    Exception(
                        jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getAvailability error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDoctorAvailability(doctorId: String): Result<Pair<List<DoctorAvailabilityDto>, List<LeaveDto>>> {
        return try {
            val url = ApiConstants.BASE_URL + ApiConstants.MANAGE_APPOINTMENTS
            val params = mapOf(
                "action" to "get_doctor_availability",
                "doc_id" to doctorId,
                "close" to "close"
            )
            Logger.d("OpdService", "getDoctorAvailability url : ${buildLogUrl(url, params)}")
            val response = client.post(url) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    buildFormBody(
                        params
                    )
                )
            }
            val responseBody = response.bodyAsText()
            Logger.d("OpdService", "getDoctorAvailability: $responseBody")
            val jsonObject = json.decodeFromString<JsonObject>(responseBody)
            if (
                jsonObject["response"]?.jsonPrimitive?.boolean == true &&
                jsonObject["message"]?.jsonPrimitive?.content == "OK"
            ) {
                val availabilityList =
                    json.decodeFromString<List<DoctorAvailabilityDto>>(jsonObject["availability"].toString())
                val leaveList =
                    json.decodeFromString<List<LeaveDto>>(jsonObject["leaves"].toString())
                Result.success(availabilityList to leaveList)
            } else {
                Result.failure(
                    Exception(
                        jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            Logger.e("OpdService", "getDoctorAvailability error: ${e.message}")
            Result.failure(e)
        }
    }


}