package com.rotary.hospital.feature.opd.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDetails(
    val mobileNumber: String,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val roomNumber: String,
    val specialization: String,
    val opdCharges: String,
    val opdDuration: String,
    val startTime: String,
    val paymentStatus: String,
    val cancelReason: String?,
    val statusMessage: String?,
    val orderId: String,
    val transactionId: String,
    val paymentId: String,
    val opdId: String?,
    val tokenNumber: String?,
    val registrationDate: String?,
    val estimatedTime: String?
)
