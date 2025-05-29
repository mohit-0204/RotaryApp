package com.rotary.hospital.feature.patient.domain.usecase

import com.rotary.hospital.feature.patient.data.model.PatientRegistrationResponse
import com.rotary.hospital.feature.patient.domain.repository.PatientRepository
import kotlin.Result

class RegisterPatientUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(
        mobileNumber: String,
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
    ): Result<PatientRegistrationResponse> {
        return try {
            val response = repository.registerPatient(
                mobileNumber, name, guardianType, guardianName, gender, age,
                bloodGroup, email, address, city, state
            )
            if (response.response) {
                Result.success(response)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}