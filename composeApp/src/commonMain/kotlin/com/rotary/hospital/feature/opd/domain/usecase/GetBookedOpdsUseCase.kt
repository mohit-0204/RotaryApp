package com.rotary.hospital.feature.opd.domain.usecase

import com.rotary.hospital.feature.opd.data.model.*
import com.rotary.hospital.feature.opd.domain.repository.OpdRepository
import kotlin.Result

class GetBookedOpdsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<List<Opd>> {
        return try {
            val opds = repository.getBookedOpds(mobileNumber)
            Result.success(opds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetRegisteredPatientsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<List<Patient>> {
        return try {
            val patients = repository.getRegisteredPatients(mobileNumber)
            Result.success(patients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetSpecializationsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(): Result<List<Specialization>> {
        return try {
            val specializations = repository.getSpecializations()
            Result.success(specializations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetDoctorsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(specialization: String): Result<List<Doctor>> {
        return try {
            val doctors = repository.getDoctors(specialization)
            Result.success(doctors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetSlotsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(doctorId: String): Result<List<Slot>> {
        return try {
            val slots = repository.getSlots(doctorId)
            Result.success(slots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetAvailabilityUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(doctorId: String, slotId: String): Result<Availability?> {
        return try {
            val availability = repository.getAvailability(doctorId, slotId)
            Result.success(availability)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetDoctorAvailabilityUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(doctorId: String): Result<Pair<List<DoctorAvailability>, List<Leave>>> {
        return try {
            val availability = repository.getDoctorAvailability(doctorId)
            Result.success(availability)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetPaymentReferenceUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(mobileNumber: String, amount: String, patientId: String, patientName: String, doctorName: String): Result<PaymentRequest?> {
        return try {
            val payment = repository.getPaymentReference(mobileNumber, amount, patientId, patientName, doctorName)
            Result.success(payment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetPaymentStatusUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(merchantTransactionId: String): Result<PaymentStatus> {
        return try {
            val status = repository.getPaymentStatus(merchantTransactionId)
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
        return try {
            val response = repository.insertOpd(
                patientId, patientName, mobileNumber, doctorName, doctorId, opdAmount,
                durationPerPatient, docTimeFrom, opdType, transactionId, paymentId, orderId, status, message
            )
            if (response.response && response.message == "success") {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}