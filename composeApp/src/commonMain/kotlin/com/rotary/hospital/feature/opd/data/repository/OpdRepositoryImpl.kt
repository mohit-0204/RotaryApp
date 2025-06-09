package com.rotary.hospital.feature.opd.data.repository

import com.rotary.hospital.feature.opd.data.remote.OpdService
import com.rotary.hospital.feature.opd.data.mapper.*
import com.rotary.hospital.feature.opd.domain.model.*
import com.rotary.hospital.feature.opd.domain.repository.OpdRepository

class OpdRepositoryImpl(private val service: OpdService) : OpdRepository {
    override suspend fun getBookedOpds(mobileNumber: String): Result<List<Opd>> {
        return service.getBookedOpds(mobileNumber).fold(
            onSuccess = { dtoList -> Result.success(dtoList.map { it.toDomain() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getRegisteredPatients(mobileNumber: String): Result<List<Patient>> {
        return service.getRegisteredPatients(mobileNumber).fold(
            onSuccess = { dtoList -> Result.success(dtoList.map { it.toDomain() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getSpecializations(): Result<List<Specialization>> {
        return service.getSpecializations().fold(
            onSuccess = { dtoList -> Result.success(dtoList.map { it.toDomain() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getDoctors(specialization: String): Result<List<Doctor>> {
        return service.getDoctors(specialization).fold(
            onSuccess = { dtoList -> Result.success(dtoList.map { it.toDomain() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getSlots(doctorId: String): Result<List<Slot>> {
        return service.getSlots(doctorId).fold(
            onSuccess = { dtoList -> Result.success(dtoList.map { it.toDomain() }) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getAvailability(doctorId: String, slotId: String): Result<Availability?> {
        return service.getAvailability(doctorId, slotId).fold(
            onSuccess = { dto -> Result.success(dto?.toDomain()) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getDoctorAvailability(doctorId: String): Result<Pair<List<DoctorAvailability>, List<Leave>>> {
        return service.getDoctorAvailability(doctorId).fold(
            onSuccess = { (availDtoList, leaveDtoList) ->
                Result.success(
                    availDtoList.map { it.toDomain() } to leaveDtoList.map { it.toDomain() }
                )
            },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getPaymentReference(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ): Result<PaymentRequest?> {
        return service.getPaymentReference(mobileNumber, amount, patientId, patientName, doctorName).fold(
            onSuccess = { dto -> Result.success(dto?.toDomain()) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun getPaymentStatus(merchantTransactionId: String): Result<PaymentStatus> {
        return service.getPaymentStatus(merchantTransactionId).fold(
            onSuccess = { dto -> Result.success(dto.toDomain()) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    override suspend fun insertOpd(
        patientId: String,
        patientName: String,
        mobileNumber: String,
        doctorName: String,
        doctorId: String,
        opdAmount: String,
        durationPerPatient: String,
        docTimeFrom: String,
        opdType: String,
        transactionId: String,
        paymentId: String,
        orderId: String,
        status: String,
        message: String
    ): Result<InsertOpdResponse> {
        return service.insertOpd(
            patientId, patientName, mobileNumber, doctorName, doctorId, opdAmount,
            durationPerPatient, docTimeFrom, opdType, transactionId, paymentId,
            orderId, status, message
        ).fold(
            onSuccess = { dto -> Result.success(dto.toDomain()) },
            onFailure = { error -> Result.failure(error) }
        )
    }
}