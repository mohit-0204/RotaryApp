package com.rotary.hospital.feature.opd.data.repository

import com.rotary.hospital.feature.opd.data.remote.PaymentService
import com.rotary.hospital.feature.opd.data.mapper.toDomain
import com.rotary.hospital.feature.opd.domain.model.*
import com.rotary.hospital.feature.opd.domain.repository.PaymentRepository

class PaymentRepositoryImpl(private val service: PaymentService) : PaymentRepository {
    override suspend fun getPaymentReference(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ): Result<PaymentRequest?> {
        return service.getPaymentReference(mobileNumber, amount, patientId, patientName, doctorName)
            .fold(
                onSuccess = { dto -> Result.success(dto.toDomain().getOrNull()) },
                onFailure = { error -> Result.failure(error) }
            )
    }

    override suspend fun getPaymentStatus(
        merchantTransactionId: String,
        doctorName: String, doctorId: String, docTime: String,
        durationPerPatient: String, opdType: String, orderId: String
    ): Result<PaymentStatus> {
        return service.getPaymentStatus(merchantTransactionId, doctorName, doctorId, docTime,
            durationPerPatient, opdType, orderId).fold(
            onSuccess = { dto -> dto.toDomain() },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun insertOpd(
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
    ): Result<InsertOpdResponse> {
        return service.insertOpd(
            patientId, patientName, mobileNumber, doctorName, doctorId, opdAmount,
            durationPerPatient, docTimeFrom, opdType, transactionId, paymentId,
            orderId, status, message
        ).fold(
            onSuccess = { dto -> dto.toDomain() },
            onFailure = { error -> Result.failure(error) }
        )
    }
}