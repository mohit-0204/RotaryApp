package com.rotary.hospital.feature.opd.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpdDto(
    @SerialName("opd_id") val opdId: String,
    @SerialName("opd_diseasestype") val opdType: String,
    @SerialName("p_id") val patientId: String,
    @SerialName("p_name") val patientName: String,
    @SerialName("opd_doctor") val doctor: String,
    @SerialName("opd_date") val date: String
)

@Serializable
data class PatientDto(
    @SerialName("pid1") val patientId: String? = null,
    @SerialName("p_name") val patientName: String? = null
)

@Serializable
data class SpecializationDto(
    @SerialName("data") val data: String? = null
)

@Serializable
data class DoctorDto(
    @SerialName("name") val name: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("opd_room") val opdRoom: String? = null
)

@Serializable
data class SlotDto(
    @SerialName("time_from") val timeFrom: String? = null,
    @SerialName("time_to") val timeTo: String? = null
)

@Serializable
data class AvailabilityDto(
    @SerialName("doc_charges") val docCharges: String? = null,
    @SerialName("doc_online_charges") val docOnlineCharges: String? = null,
    @SerialName("doc_time_from") val docTimeFrom: String? = null,
    @SerialName("doc_time_to") val docTimeTo: String? = null,
    @SerialName("approximate_time") val approximateTime: String? = null,
    @SerialName("doc_duration_per_patient") val docDurationPerPatient: String? = null,
    @SerialName("doc_no_of_appointments") val docNoOfAppointments: String? = null,
    @SerialName("appointments") val appointments: String? = null
)

@Serializable
data class DoctorAvailabilityDto(
    @SerialName("doc_frequency") val docFrequency: String? = null,
    @SerialName("doc_days") val docDays: String? = null,
    @SerialName("doc_time_from") val docTimeFrom: String? = null,
    @SerialName("doc_time_to") val docTimeTo: String? = null
)

@Serializable
data class LeaveDto(
    @SerialName("cancel_date") val cancelDate: String? = null,
    @SerialName("cancel_date_to") val cancelDateTo: String? = null
)

@Serializable
data class PaymentRequestDto(
    @SerialName("api_endpoint") val apiEndPoint: String? = null,
    @SerialName("payload_base64") val payloadBase64: String? = null,
    @SerialName("checksum") val checksum: String? = null,
    @SerialName("merchant_transaction_id") val merchantTransactionId: String? = null
)

@Serializable
data class PaymentStatusDto(
    @SerialName("response") val response: String? = null,
    @SerialName("message_code") val messageCode: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("transaction_id") val transactionId: String? = null
)

@Serializable
data class InsertOpdResponseDto(
    @SerialName("response") val response: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("opd_id") val opdId: String? = null,
    @SerialName("opd_date") val opdDate: String? = null,
    @SerialName("token_number") val tokenNumber: String? = null,
    @SerialName("estimated_time") val estimatedTime: String? = null
)