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
data class OpdDetailsDto(
    @SerialName("opd_id") val opdId: String,
    @SerialName("opd_date") val opdDate: String,
    @SerialName("opd_token") val tokenNumber: String,
    @SerialName("estimated_time") val estimatedTime: String,
    @SerialName("opd_charge") val opdCharges: String,
    @SerialName("p_id") val patientId: String,
    @SerialName("p_name") val patientName: String,
    @SerialName("opd_doctor") val doctor: String,
    @SerialName("transaction_id") val transactionId: String,
    @SerialName("order_id") val orderId: String,
    @SerialName("payment_id") val paymentId: String,
    @SerialName("transaction_status") val transactionStatus: String,
    @SerialName("transaction_message") val transactionMessage: String
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
    @SerialName("doc_name") val name: String? = null,
    @SerialName("doc_id") val id: String? = null,
    @SerialName("doc_opdroom") val opdRoom: String? = null
)

@Serializable
data class SlotDto(
    @SerialName("doc_time_from") val timeFrom: String? = null,
    @SerialName("doc_time_to") val timeTo: String? = null
)

@Serializable
data class AvailabilityDto(
    @SerialName("doc_charges") val docCharges: Int,
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
    @SerialName("apiEndPoint") val apiEndPoint: String? = null,
    @SerialName("payloadBase64") val payloadBase64: String? = null,
    @SerialName("checksum") val checksum: String? = null,
    @SerialName("merchantTransactionId") val merchantTransactionId: String? = null
)

@Serializable
data class PaymentStatusDto(
    @SerialName("response") val response: String? = null,
    @SerialName("message_code") val messageCode: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("transactionId") val transactionId: String? = null,
    @SerialName("opd_id") val opdId: Int? = null,
    @SerialName("token_number") val tokenNumber: Int? = null,
    @SerialName("opd_date") val registrationDate: String? = null,
    @SerialName("estimated_time") val estimatedTime: String? = null
)