package com.rotary.hospital.feature.patient.domain.usecase

import com.rotary.hospital.feature.patient.data.model.ApiPatient
import com.rotary.hospital.feature.patient.data.model.ApiPatientProfile
import com.rotary.hospital.feature.patient.domain.repository.PatientRepository

class GetPatientProfileUseCase(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(patientId: String): Result<ApiPatientProfile> {
        return try {
            val response = repository.getPatientProfile(patientId)
            if (response.response && response.data.isNotEmpty()) {
                Result.success(response.data.first())
            } else {
                Result.failure(Exception("Patient profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
