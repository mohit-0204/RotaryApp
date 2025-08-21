package com.rotary.hospital.feature.opd.domain.model

import kotlinx.serialization.Serializable

data class Opd(
    val opdId: String,
    val opdType: String,
    val patientId: String,
    val patientName: String,
    val doctor: String,
    val date: String
)

data class OpdDetails(
    val opdId: String,
    val opdDate: String,
    val tokenNumber: String,
    val estimatedTime: String,
    val opdCharges: String,
    val patientId: String,
    val patientName: String,
    val doctor: String,
    val transactionId: String,
    val orderId: String,
    val paymentId: String,
    val transactionStatus: String,
    val transactionMessage: String
)

@Serializable
data class Patient(
    val patientId: String,
    val patientName: String
)

@Serializable
data class Specialization(
    val data: String
)

@Serializable
data class Doctor(
    val name: String,
    val id: String,
    val opdRoom: String
)

@Serializable
data class Slot(
    val timeFrom: String,
    val timeTo: String
)

@Serializable
data class Availability(
    val docCharges: Int,
    val docOnlineCharges: String,
    val docTimeFrom: String,
    val docTimeTo: String,
    val approximateTime: String,
    val docDurationPerPatient: String,
    val docNoOfAppointments: String,
    val appointments: String,
    val available: Boolean = run { // Computed property
        val total = docNoOfAppointments.toIntOrNull() ?: 0
        val booked = appointments.toIntOrNull() ?: 0
        total > booked
    }
)

@Serializable
data class DoctorAvailability(
    val docFrequency: String,
    val docDays: String,
    val docTimeFrom: String,
    val docTimeTo: String
)

@Serializable
data class Leave(
    val cancelDate: String,
    val cancelDateTo: String
)

@Serializable
data class PaymentRequest(
    val apiEndPoint: String,
    val payloadBase64: String,
    val checksum: String,
    val merchantTransactionId: String
)

@Serializable
data class PaymentStatus(
    val response: String,
    val messageCode: String,
    val message: String,
    val transactionId: String
){
    val isSuccess: Boolean
        get() = response == "true" && messageCode == "PAYMENT_SUCCESS"
    val isPending: Boolean
        get() = messageCode == "PAYMENT_PENDING"
    val isFailure: Boolean
        get() = !isSuccess && !isPending
}

@Serializable
data class InsertOpdResponse(
    val response: Boolean,
    val message: String,
    val opdId: String? = null,
    val opdDate: String? = null,
    val tokenNumber: String? = null,
    val estimatedTime: String? = null
)