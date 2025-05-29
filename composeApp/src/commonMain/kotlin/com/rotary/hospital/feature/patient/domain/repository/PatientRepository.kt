package com.rotary.hospital.feature.patient.domain.repository

import com.rotary.hospital.core.data.model.Patient
import com.rotary.hospital.feature.patient.data.model.PatientRegistrationResponse

interface PatientRepository {
    suspend fun registerPatient(
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
    ): PatientRegistrationResponse

    suspend fun getRegisteredPatients(mobileNumber: String): List<Patient>
}