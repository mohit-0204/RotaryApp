package com.rotary.hospital.feature.patient.data.repository

import com.rotary.hospital.core.data.model.Patient
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
    ): PatientRegistrationResponse {
        return patientService.registerPatient(
            mobileNumber, name, guardianType, guardianName, gender, age,
            bloodGroup, email, address, city, state
        )
    }

    override suspend fun getRegisteredPatients(mobileNumber: String): List<Patient> {
        return patientService.getRegisteredPatients(mobileNumber).data.map { apiPatient ->
            Patient(
                id = apiPatient.id.trim(),
                name = apiPatient.name.trim(),
                phoneNumber = mobileNumber.trim()
            )
        }
    }

    override suspend fun getPatientProfile(patientId: String): PatientProfileResponse {
        return patientService.getPatientProfile(patientId)
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
    ): Boolean {
        return patientService.updatePatientProfile(
            mobileNumber, patientId, name, guardianType, guardianName, gender, age,
            bloodGroup, email, address, city, state
        )
    }
}