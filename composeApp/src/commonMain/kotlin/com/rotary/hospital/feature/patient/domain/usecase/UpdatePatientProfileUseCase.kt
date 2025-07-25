package com.rotary.hospital.feature.patient.domain.usecase

import com.rotary.hospital.feature.patient.data.model.PatientRegistrationResponse
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
        return try {
            val response = repository.updatePatientProfile(
                mobileNumber, patientId, name, guardianType, guardianName, gender,
                age, bloodGroup, email, address, city, state
            )
            if (response) {
                Result.success(response)
            } else {
                Result.failure(Exception("Update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
