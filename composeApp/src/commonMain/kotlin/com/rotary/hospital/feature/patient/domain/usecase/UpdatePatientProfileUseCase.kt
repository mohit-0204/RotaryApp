package com.rotary.hospital.feature.patient.domain.usecase

import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.feature.patient.domain.repository.PatientRepository

class UpdatePatientProfileUseCase(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(
        mobileNumber: String,
        patientId: String,
        name: String,
        guardianType: String,
        guardianName: String,
        gender: String,
        age: String,
        bloodGroup: String,
        email: String,
        address: String,
        city: String,
        state: String
    ): Result<Boolean> {
        return repository.updatePatientProfile(
            mobileNumber, patientId, name, guardianType, guardianName, gender,
            age, bloodGroup, email, address, city, state
        )
    }
}