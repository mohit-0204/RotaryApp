package com.rotary.hospital.feature.patient.domain.usecase

import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.feature.patient.data.model.PatientRegistrationResponse
import com.rotary.hospital.feature.patient.domain.repository.PatientRepository

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
        return repository.registerPatient(
            mobileNumber, name, guardianType, guardianName, gender, age,
            bloodGroup, email, address, city, state
        )
    }
}