package com.rotary.hospital.feature.opd.domain.usecase

import com.rotary.hospital.feature.opd.domain.model.*
import com.rotary.hospital.feature.opd.domain.repository.OpdRepository
import com.rotary.hospital.feature.opd.domain.repository.PaymentRepository
import kotlin.math.round
import kotlin.text.*

class GetBookedOpdsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<List<Opd>> {
        return repository.getBookedOpds(mobileNumber)
    }
}

class GetRegisteredPatientsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<List<Patient>> {
        return repository.getRegisteredPatients(mobileNumber)
    }
}

class GetSpecializationsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(): Result<List<Specialization>> {
        return repository.getSpecializations()
    }
}

class GetDoctorsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(specialization: String): Result<List<Doctor>> {
        return repository.getDoctors(specialization)
    }
}

class GetSlotsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(doctorId: String): Result<List<Slot>> {
        return repository.getSlots(doctorId)
    }
}

class GetAvailabilityUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(doctorId: String, slotId: String): Result<Availability?> {
        return repository.getAvailability(doctorId, slotId)
    }
}

class GetDoctorAvailabilityUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(doctorId: String): Result<Pair<List<DoctorAvailability>, List<Leave>>> {
        return repository.getDoctorAvailability(doctorId)
    }
}

class GetPaymentReferenceUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ): Result<PaymentRequest?> {
        if (amount.toDoubleOrNull()?.let { it <= 0 } == true) {
            return Result.failure(Exception("Invalid amount: must be positive"))
        }

        return repository.getPaymentReference(
            mobileNumber,
            formatAmount(amount.toDoubleOrNull() ?: 0.0),
            patientId,
            patientName,
            doctorName
        )
    }
}

class GetPaymentStatusUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(merchantTransactionId: String): Result<PaymentStatus> {
        if (merchantTransactionId.isBlank()) {
            return Result.failure(Exception("Invalid transaction ID"))
        }
        return repository.getPaymentStatus(merchantTransactionId)
    }
}

class InsertOpdUseCase(private val repository: PaymentRepository) {
    enum class Status { SUCCESS, FAILURE }

    suspend operator fun invoke(
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
        val response = repository.insertOpd(
            patientId, patientName, mobileNumber, doctorName, doctorId, opdAmount,
            durationPerPatient, docTimeFrom, opdType, transactionId, paymentId,
            orderId, status, message
        )
        return response.fold(
            onSuccess = { dto ->
                if (dto.response && dto.message == Status.SUCCESS.name.lowercase()) {
                    Result.success(dto)
                } else {
                    Result.failure(Exception(dto.message))
                }
            },
            onFailure = { error -> Result.failure(error) }
        )
    }
}

private fun formatAmount(value: Double): String {
    return (round(value * 100) / 100).toString()
}
