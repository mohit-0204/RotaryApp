package com.rotary.hospital.feature.opd.domain.usecase

import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.core.payment.PaymentResult
import com.rotary.hospital.feature.opd.domain.model.PaymentRequest
import com.rotary.hospital.feature.opd.domain.model.PaymentStatus
import com.rotary.hospital.feature.opd.domain.repository.PaymentRepository
import com.rotary.hospital.feature.opd.presentation.model.TransactionDetails
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
        roomNumber: String,
        specialization: String,
        doctorId: String,
        durationPerPatient: String,
        docTimeFrom: String,
        opdType: String
    ): Flow<PaymentFlowResult> = flow {
        emit(PaymentFlowResult.Loading)

        // Step 1: Get payment reference
        val referenceResult: Result<PaymentRequest?> = paymentRepository.getPaymentReference(
            mobileNumber, amount, patientId, patientName, doctorName,
            doctorId, docTimeFrom, durationPerPatient, opdType, patientId
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
        val paymentResult: PaymentResult = suspendCoroutine { continuation ->
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
                val statusResult: Result<PaymentStatus> =
                    /*Result.success(
                        PaymentStatus(
                            "true",
                            "PAYMENT_SUCCESS",
                            "success",
                            "T2508291141105345404867",
                            opdId = "",
                            tokenNumber = "",
                            registrationDate = "",
                            estimatedTime = ""
                        )
                    )*/
                paymentRepository.getPaymentStatus(
                    paymentRequest.merchantTransactionId,
                    doctorName, doctorId, docTimeFrom, durationPerPatient, opdType,
                    paymentRequest.merchantTransactionId
                )
                statusResult.fold(
                    onSuccess = { status -> // this is payment result object carrying status(pass/fail/pending etc)
                        var transactionDetails =
                            TransactionDetails(
                                mobileNumber = mobileNumber,
                                patientId = patientId,
                                patientName = patientName,
                                doctorId = doctorId,
                                doctorName = doctorName,
                                roomNumber = roomNumber,
                                specialization = specialization,
                                opdCharges = amount,
                                opdDuration = durationPerPatient,
                                startTime = docTimeFrom,
                                paymentStatus = status.messageCode,
                                cancelReason = null,
                                statusMessage = status.message,
                                orderId = paymentRequest.merchantTransactionId,
                                transactionId = status.transactionId,
                                paymentId = paymentRequest.merchantTransactionId,
                                opdId = null,
                                tokenNumber = null,
                                registrationDate = null,
                                estimatedTime = null
                            )
                        if (status.isSuccess) { // if payment is success, opd will be inserted
                            transactionDetails = transactionDetails.copy(
                                opdId = status.opdId,
                                tokenNumber = status.tokenNumber,
                                registrationDate = status.registrationDate,
                                estimatedTime = status.estimatedTime
                            )
                            emit(PaymentFlowResult.Success(transactionDetails))
                        } else if (status.isPending) {
                            transactionDetails = transactionDetails.copy(
                                cancelReason = "Payment pending"
                            )
                            emit(PaymentFlowResult.Pending(transactionDetails))
                        } else {
                            transactionDetails = transactionDetails.copy(
                                cancelReason = "Payment failed"
                            )
                            emit(PaymentFlowResult.Failed(transactionDetails))
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
                emit(PaymentFlowResult.Cancelled)
            }
        }
    }
}

sealed interface PaymentFlowResult {
    object Loading : PaymentFlowResult
    data class Success(val successResponse: TransactionDetails) :
        PaymentFlowResult

    data class Pending(val pendingResponse: TransactionDetails) :
        PaymentFlowResult

    data class Failed(val failedResponse: TransactionDetails) :
        PaymentFlowResult

    data class Error(val message: String) : PaymentFlowResult
    object Cancelled : PaymentFlowResult // New state for user cancellation
}