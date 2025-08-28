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

class GetOpdDetailsUseCase(private val repository: OpdRepository) {
    suspend operator fun invoke(opdId: String): Result<OpdDetails?> {
        return repository.getOpdDetails(opdId)
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
