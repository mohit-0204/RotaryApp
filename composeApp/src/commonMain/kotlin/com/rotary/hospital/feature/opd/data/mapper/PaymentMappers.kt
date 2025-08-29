package com.rotary.hospital.feature.opd.data.mapper

import com.rotary.hospital.feature.opd.data.remote.dto.PaymentRequestDto
import com.rotary.hospital.feature.opd.data.remote.dto.PaymentStatusDto
import com.rotary.hospital.feature.opd.domain.model.PaymentRequest
import com.rotary.hospital.feature.opd.domain.model.PaymentStatus

fun PaymentRequestDto.toDomain(): Result<PaymentRequest> {
    return try {
        Result.success(
            PaymentRequest(
                apiEndPoint = apiEndPoint
                    ?: return Result.failure(Exception("API endpoint missing")),
                payloadBase64 = payloadBase64
                    ?: return Result.failure(Exception("Payload missing")),
                checksum = checksum ?: return Result.failure(Exception("Checksum missing")),
                merchantTransactionId = merchantTransactionId
                    ?: return Result.failure(Exception("Transaction ID missing"))
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun PaymentStatusDto.toDomain(): Result<PaymentStatus> {
    return try {
        Result.success(
            PaymentStatus(
                response = response ?: return Result.failure(Exception("Response missing")),
                messageCode = messageCode
                    ?: return Result.failure(Exception("Message code missing")),
                message = message ?: return Result.failure(Exception("Message missing")),
                transactionId = transactionId
                    ?: return Result.failure(Exception("Transaction ID missing")),
                opdId = opdId?.toString() ?: "",
                tokenNumber = tokenNumber?.toString() ?: "",
                registrationDate = registrationDate,
                estimatedTime = estimatedTime
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}