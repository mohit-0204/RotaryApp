package com.rotary.hospital.feature.opd.domain.repository

import com.rotary.hospital.feature.opd.domain.model.*

interface PaymentRepository {
    suspend fun getPaymentReference(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ): Result<PaymentRequest?>

    suspend fun getPaymentStatus(
        merchantTransactionId: String, doctorName: String, doctorId: String, docTime: String,
        durationPerPatient: String, opdType: String, orderId: String
    ): Result<PaymentStatus>

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
    ): Result<InsertOpdResponse>
}