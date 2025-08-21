package com.rotary.hospital.feature.opd.domain.repository

import com.rotary.hospital.feature.opd.domain.model.*

interface OpdRepository {
    suspend fun getBookedOpds(mobileNumber: String): Result<List<Opd>>
    suspend fun getOpdDetails(opdId: String): Result<OpdDetails?>
    suspend fun getRegisteredPatients(mobileNumber: String): Result<List<Patient>>
    suspend fun getSpecializations(): Result<List<Specialization>>
    suspend fun getDoctors(specialization: String): Result<List<Doctor>>
    suspend fun getSlots(doctorId: String): Result<List<Slot>>
    suspend fun getAvailability(doctorId: String, slotId: String): Result<Availability?>
    suspend fun getDoctorAvailability(doctorId: String): Result<Pair<List<DoctorAvailability>, List<Leave>>>
}