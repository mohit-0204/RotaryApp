package com.rotary.hospital.feature.patient.data.repository

import com.rotary.hospital.core.data.model.Patient
import com.rotary.hospital.core.domain.AuthError
import com.rotary.hospital.core.domain.PatientError
import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.feature.patient.data.model.PatientProfileResponse
import com.rotary.hospital.feature.patient.data.model.PatientRegistrationResponse
import com.rotary.hospital.feature.patient.data.network.PatientService
import com.rotary.hospital.feature.patient.domain.repository.PatientRepository

class PatientRepositoryImpl(private val patientService: PatientService) : PatientRepository {
    override suspend fun registerPatient(
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
        return when (val result = patientService.registerPatient(
            mobileNumber, name, guardianType, guardianName, gender, age,
            bloodGroup, email, address, city, state
        )) {
            is Result.Success -> {
                if (result.data.response) {
                    result
                } else {
                    Result.Error(PatientError.RegistrationFailed)
                }
            }

            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getRegisteredPatients(mobileNumber: String): Result<List<Patient>> {
        return when (val result = patientService.getRegisteredPatients(mobileNumber)) {
            is Result.Success -> {
                if (result.data.response) {
                    val patients = result.data.data.map { apiPatient ->
                        Patient(
                            id = apiPatient.id.trim(),
                            name = apiPatient.name.trim(),
                            phoneNumber = mobileNumber.trim()
                        )
                    }
                    Result.Success(patients)
                } else {
                    Result.Error(PatientError.NoPatientsFound)
                }
            }

            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getPatientProfile(patientId: String): Result<PatientProfileResponse> {
        return when (val result = patientService.getPatientProfile(patientId)) {
            is Result.Success -> {
                if (result.data.response) {
                    result
                } else {
                    Result.Error(PatientError.ProfileNotFound)
                }
            }

            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun updatePatientProfile(
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
        return when (val result = patientService.updatePatientProfile(
            mobileNumber, patientId, name, guardianType, guardianName, gender, age,
            bloodGroup, email, address, city, state
        )) {
            is Result.Success -> {
                if (result.data.response) {
                    Result.Success(true)
                } else {
                    Result.Error(PatientError.UpdateFailed)
                }
            }

            is Result.Error -> Result.Error(result.error)
        }
    }
}