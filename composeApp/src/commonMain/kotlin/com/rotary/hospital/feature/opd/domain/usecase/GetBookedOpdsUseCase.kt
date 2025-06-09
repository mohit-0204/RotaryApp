package com.rotary.hospital.feature.opd.domain.usecase

import com.rotary.hospital.feature.opd.domain.model.Availability
import com.rotary.hospital.feature.opd.domain.model.Doctor
import com.rotary.hospital.feature.opd.domain.model.DoctorAvailability
import com.rotary.hospital.feature.opd.domain.model.InsertOpdResponse
import com.rotary.hospital.feature.opd.domain.model.Leave
import com.rotary.hospital.feature.opd.domain.model.Opd
import com.rotary.hospital.feature.opd.domain.model.Patient
import com.rotary.hospital.feature.opd.domain.model.PaymentRequest
import com.rotary.hospital.feature.opd.domain.model.PaymentStatus
import com.rotary.hospital.feature.opd.domain.model.Slot
import com.rotary.hospital.feature.opd.domain.model.Specialization
import com.rotary.hospital.feature.opd.domain.repository.OpdRepository
import kotlin.Result

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

class GetPaymentReferenceUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ): Result<PaymentRequest?> {
        return repository.getPaymentReference(
            mobileNumber, amount, patientId, patientName, doctorName
        )
    }
}

class GetPaymentStatusUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(merchantTransactionId: String): Result<PaymentStatus> {
        return repository.getPaymentStatus(merchantTransactionId)
    }
}

class InsertOpdUseCase(private val repository: OpdRepository) {
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
            patientId,
            patientName,
            mobileNumber,
            doctorName,
            doctorId,
            opdAmount,
            durationPerPatient,
            docTimeFrom,
            opdType,
            transactionId,
            paymentId,
            orderId,
            status,
            message
        )
        return if (response.isSuccess && response.getOrNull()?.response == true && response.getOrNull()?.message == "success") {
            response
        } else {
            Result.failure(Exception(response.getOrNull()?.message ?: "Unknown error"))
        }
    }
}