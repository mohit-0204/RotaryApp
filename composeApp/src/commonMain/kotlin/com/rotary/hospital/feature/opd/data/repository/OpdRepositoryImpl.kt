package com.rotary.hospital.feature.opd.data.repository

import com.rotary.hospital.feature.opd.data.remote.OpdService
import com.rotary.hospital.feature.opd.data.mapper.*
import com.rotary.hospital.feature.opd.domain.model.*
import com.rotary.hospital.feature.opd.domain.repository.OpdRepository

class OpdRepositoryImpl(private val service: OpdService) : OpdRepository {
    override suspend fun getBookedOpds(mobileNumber: String): Result<List<Opd>> {
        return service.getBookedOpds(mobileNumber).fold(
            onSuccess = { dtoList -> Result.success(dtoList.mapNotNull { it.toDomain().getOrNull() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getRegisteredPatients(mobileNumber: String): Result<List<Patient>> {
        return service.getRegisteredPatients(mobileNumber).fold(
            onSuccess = { dtoList -> Result.success(dtoList.mapNotNull { it.toDomain().getOrNull() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getSpecializations(): Result<List<Specialization>> {
        return service.getSpecializations().fold(
            onSuccess = { dtoList -> Result.success(dtoList.mapNotNull { it.toDomain().getOrNull() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getDoctors(specialization: String): Result<List<Doctor>> {
        return service.getDoctors(specialization).fold(
            onSuccess = { dtoList -> Result.success(dtoList.mapNotNull { it.toDomain().getOrNull() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getSlots(doctorId: String): Result<List<Slot>> {
        return service.getSlots(doctorId).fold(
            onSuccess = { dtoList -> Result.success(dtoList.mapNotNull { it.toDomain().getOrNull() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getAvailability(doctorId: String, slotId: String): Result<Availability?> {
        return service.getAvailability(doctorId, slotId).fold(
            onSuccess = { dto -> Result.success(dto?.toDomain()?.getOrNull()) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getDoctorAvailability(doctorId: String): Result<Pair<List<DoctorAvailability>, List<Leave>>> {
        return service.getDoctorAvailability(doctorId).fold(
            onSuccess = { (availDtoList, leaveDtoList) ->
                Result.success(
                    availDtoList.mapNotNull { it.toDomain().getOrNull() } to
                            leaveDtoList.mapNotNull { it.toDomain().getOrNull() }
                )
            },
            onFailure = { error -> Result.failure(error) }
        )
    }
}