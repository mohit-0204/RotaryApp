package com.rotary.hospital.feature.opd.domain.repository

import com.rotary.hospital.feature.opd.domain.model.*

interface OpdRepository {
    suspend fun getBookedOpds(mobileNumber: String): Result<List<Opd>>
    suspend fun getRegisteredPatients(mobileNumber: String): Result<List<Patient>>
    suspend fun getSpecializations(): Result<List<Specialization>>
    suspend fun getDoctors(specialization: String): Result<List<Doctor>>
    suspend fun getSlots(doctorId: String): Result<List<Slot>>
    suspend fun getAvailability(doctorId: String, slotId: String): Result<Availability?>
    suspend fun getDoctorAvailability(doctorId: String): Result<Pair<List<DoctorAvailability>, List<Leave>>>
    suspend fun getPaymentReference(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ): Result<PaymentRequest?>
    suspend fun getPaymentStatus(merchantTransactionId: String): Result<PaymentStatus>
    suspend fun insertOpd(
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
    ): Result<InsertOpdResponse>
}