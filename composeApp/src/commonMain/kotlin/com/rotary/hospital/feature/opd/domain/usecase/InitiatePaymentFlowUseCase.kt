package com.rotary.hospital.feature.opd.domain.usecase

import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.core.payment.PaymentResult
import com.rotary.hospital.feature.opd.domain.model.InsertOpdResponse
import com.rotary.hospital.feature.opd.domain.model.PaymentStatus
import com.rotary.hospital.feature.opd.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.suspendCoroutine

class InitiatePaymentFlowUseCase(
    private val paymentRepository: PaymentRepository,
) {
    operator fun invoke(
        paymentHandler: PaymentHandler,
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String,
        doctorId: String,
        durationPerPatient: String,
        docTimeFrom: String,
        opdType: String
    ): Flow<PaymentFlowResult> = flow {
        emit(PaymentFlowResult.Loading)

        // Step 1: Get payment reference
        val referenceResult = paymentRepository.getPaymentReference(
            mobileNumber, amount, patientId, patientName, doctorName
        )
        val paymentRequest = referenceResult.getOrElse {
            emit(PaymentFlowResult.Error("Failed to get payment reference: ${it.message}"))
            Logger.e("ERROR", "Failed to get payment reference: ${it.message}")
            return@flow
        } ?: run {
            Logger.e("ERROR", "No payment reference received")
            emit(PaymentFlowResult.Error("No payment reference received"))
            return@flow
        }

        // Step 2: Start payment using suspendCoroutine to bridge callback
        val paymentResult = suspendCoroutine<PaymentResult> { continuation ->
            Logger.d(
                "PaymentFlow",
                "Starting payment with reference: ${paymentRequest.merchantTransactionId}"
            )
            paymentHandler.startPayment(
                base64Body = paymentRequest.payloadBase64,
                checksum = paymentRequest.checksum,
                apiEndPoint = paymentRequest.apiEndPoint
            ) { result ->
                continuation.resumeWith(Result.success(result))
            }
        }

        // Step 3: Handle payment result
        when (paymentResult) {
            is PaymentResult.Success -> {
                // Step 4: Verify payment status
                val statusResult =
                    paymentRepository.getPaymentStatus(paymentRequest.merchantTransactionId)
                statusResult.fold(
                    onSuccess = { status ->
                        if (status.isSuccess) {
                            // Step 5: Insert OPD
                            val insertResult = paymentRepository.insertOpd(
                                patientId = patientId,
                                patientName = patientName,
                                mobileNumber = mobileNumber,
                                doctorName = doctorName,
                                doctorId = doctorId,
                                opdAmount = amount,
                                durationPerPatient = durationPerPatient,
                                docTimeFrom = docTimeFrom,
                                opdType = opdType,
                                transactionId = paymentRequest.merchantTransactionId,
                                paymentId = paymentRequest.merchantTransactionId, // As per your instruction
                                orderId = paymentRequest.merchantTransactionId,
                                status = status.messageCode,
                                message = status.message
                            )
                            insertResult.fold(
                                onSuccess = { response ->
                                    emit(PaymentFlowResult.Success(response))
                                },
                                onFailure = { error ->
                                    emit(PaymentFlowResult.Error("Failed to book appointment: ${error.message}"))
                                }
                            )
                        } else if (status.isPending) {
                            emit(PaymentFlowResult.Pending(status))
                        } else {
                            emit(PaymentFlowResult.Error("Payment failed: ${status.message}"))
                        }
                    },
                    onFailure = { error ->
                        emit(PaymentFlowResult.Error("Failed to verify payment: ${error.message}"))
                    }
                )
            }

            is PaymentResult.Failure -> {
                Logger.e("PaymentFlow", "Payment failed")
                emit(PaymentFlowResult.Error("Payment failed: ${paymentResult.message}"))
            }

            PaymentResult.Cancelled -> {
                Logger.e("PaymentFlow", "Payment cancelled")
                emit(PaymentFlowResult.Error("Payment cancelled by user"))
            }
        }
    }
}

sealed interface PaymentFlowResult {
    object Loading : PaymentFlowResult
    data class Success(val response: InsertOpdResponse) : PaymentFlowResult
    data class Pending(val status: PaymentStatus) : PaymentFlowResult
    data class Error(val message: String) : PaymentFlowResult
}