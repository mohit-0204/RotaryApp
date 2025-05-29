package com.rotary.hospital.feature.patient.domain.usecase

import com.rotary.hospital.core.data.model.Patient
import com.rotary.hospital.feature.patient.domain.repository.PatientRepository

class GetRegisteredPatientsUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<List<Patient>> {
        return try {
            val patients = repository.getRegisteredPatients(mobileNumber)
            Result.success(patients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}