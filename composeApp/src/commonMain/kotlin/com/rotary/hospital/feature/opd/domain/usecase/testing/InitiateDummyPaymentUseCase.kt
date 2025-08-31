package com.rotary.hospital.feature.opd.domain.usecase.testing

import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.core.payment.PaymentResult
import com.rotary.hospital.feature.opd.domain.model.PaymentRequest
import com.rotary.hospital.feature.opd.domain.model.PaymentStatus
import com.rotary.hospital.feature.opd.domain.repository.PaymentRepository
import com.rotary.hospital.feature.opd.domain.usecase.PaymentFlowResult
import com.rotary.hospital.feature.opd.presentation.model.TransactionDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InitiateDummyPaymentUseCase(
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

        // HARDCODED SIMULATION TYPE: Change to "SUCCESS", "PENDING", or "FAILED" for testing
        val simulationType = "FAILED" // "SUCCESS" or "PENDING" or "FAILED"

        // Simulate Step 1: Hardcode a dummy payment reference (no real repo call)
        val merchantTransactionId = "SIMULATED_TXN_1234567890"
        val paymentRequest = PaymentRequest(
            apiEndPoint = "simulated_endpoint",
            payloadBase64 = "simulated_base64",
            checksum = "simulated_checksum",
            merchantTransactionId = merchantTransactionId
        )
        Logger.d("Simulation", "Simulated payment reference: $merchantTransactionId")

        // Skip Step 2: No actual paymentHandler call, assume PaymentResult.Success
        val paymentResult: PaymentResult = PaymentResult.Success
        Logger.d("Simulation", "Simulated payment result: Success (bypassing actual payment)")

        // Simulate Step 3 & 4: Handle simulated result with static PaymentStatus and TransactionDetails
        when (paymentResult) {
            is PaymentResult.Success -> {
                when (simulationType) {
                    "SUCCESS" -> {
                        val simulatedStatus = PaymentStatus(
                            response = "true",
                            messageCode = "PAYMENT_SUCCESS",
                            message = "success",
                            transactionId = "T2508281750479145404288",
                            opdId = "548788",
                            tokenNumber = "6",
                            registrationDate = "2025-08-28",
                            estimatedTime = "17:23:32"
                        )
                        val transactionDetails = TransactionDetails(
                            mobileNumber = "1234567890",
                            patientId = "0000240319533",
                            patientName = "Rahul Bhandari",
                            doctorId = "149",
                            doctorName = "Dr. Dummy",
                            roomNumber = "Room101",
                            specialization = "Cardiology",
                            opdCharges = "1000",
                            opdDuration = "15",
                            startTime = "17:00:00",
                            paymentStatus = "PAYMENT_SUCCESS",
                            cancelReason = null,
                            statusMessage = "success",
                            orderId = "MI1756381389",
                            transactionId = "T2508281750479145404288",
                            paymentId = "MI1756381389",
                            opdId = "548788",
                            tokenNumber = "6",
                            registrationDate = "2025-08-28",
                            estimatedTime = "17:23:32"
                        )
                        emit(PaymentFlowResult.Success(transactionDetails))
                    }
                    "PENDING" -> {
                        val simulatedStatus = PaymentStatus(
                            response = "false",
                            messageCode = "PAYMENT_PENDING",
                            message = "Payment is pending",
                            transactionId = "T2508291538367815404282",
                            opdId = null,
                            tokenNumber = null,
                            registrationDate = null,
                            estimatedTime = null
                        )
                        val transactionDetails = TransactionDetails(
                            mobileNumber = "1234567890",
                            patientId = "0000240319533",
                            patientName = "Rahul Bhandari",
                            doctorId = "149",
                            doctorName = "Dr. Dummy",
                            roomNumber = "Room101",
                            specialization = "Cardiology",
                            opdCharges = "1000",
                            opdDuration = "15",
                            startTime = "17:00:00",
                            paymentStatus = "PAYMENT_PENDING",
                            cancelReason = "Payment pending",
                            statusMessage = "Payment is pending",
                            orderId = "MI1756381390",
                            transactionId = "T2508291538367815404282",
                            paymentId = "MI1756381390",
                            opdId = null,
                            tokenNumber = null,
                            registrationDate = null,
                            estimatedTime = null
                        )
                        emit(PaymentFlowResult.Pending(transactionDetails))
                    }
                    "FAILED" -> {
                        val simulatedStatus = PaymentStatus(
                            response = "false",
                            messageCode = "PAYMENT_FAILED",
                            message = "Payment failed due to some error",
                            transactionId = "T2508291538367815404282",
                            opdId = null,
                            tokenNumber = null,
                            registrationDate = null,
                            estimatedTime = null
                        )
                        val transactionDetails = TransactionDetails(
                            mobileNumber = "1234567890",
                            patientId = "0000240319533",
                            patientName = "Rahul Bhandari",
                            doctorId = "149",
                            doctorName = "Dr. Dummy",
                            roomNumber = "Room101",
                            specialization = "Cardiology",
                            opdCharges = "1000",
                            opdDuration = "15",
                            startTime = "17:00:00",
                            paymentStatus = "PAYMENT_FAILED",
                            cancelReason = "Payment failed",
                            statusMessage = "Payment failed due to some error",
                            orderId = "MI1756381391",
                            transactionId = "T250829153836781540 Republic4282",
                            paymentId = "MI1756381391",
                            opdId = null,
                            tokenNumber = null,
                            registrationDate = null,
                            estimatedTime = null
                        )
                        emit(PaymentFlowResult.Failed(transactionDetails))
                    }
                    else -> throw IllegalArgumentException("Invalid simulationType")
                }
            }

            is PaymentResult.Failure -> {
                Logger.e("PaymentFlow", "Payment failed")
                emit(PaymentFlowResult.Error("Payment failed: ${(paymentResult as PaymentResult.Failure).message}"))
            }

            PaymentResult.Cancelled -> {
                Logger.e("PaymentFlow", "Payment cancelled")
                emit(PaymentFlowResult.Cancelled)
            }
        }
    }
}