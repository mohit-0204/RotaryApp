package com.rotary.hospital.feature.patient.domain.usecase

import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.feature.patient.data.model.PatientProfileResponse
import com.rotary.hospital.feature.patient.domain.repository.PatientRepository

class GetPatientProfileUseCase(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(patientId: String): Result<PatientProfileResponse> {
        return repository.getPatientProfile(patientId)
    }
}