package com.rotary.hospital.feature.opd.domain.repository

import com.rotary.hospital.feature.opd.data.model.*
interface OpdRepository {
    suspend fun getBookedOpds(mobileNumber: String): List<Opd>
    suspend fun getRegisteredPatients(mobileNumber: String): List<Patient>
    suspend fun getSpecializations(): List<Specialization>
    suspend fun getDoctors(specialization: String): List<Doctor>
    suspend fun getSlots(doctorId: String): List<Slot>
    suspend fun getAvailability(doctorId: String, slotId: String): Availability?
    suspend fun getDoctorAvailability(doctorId: String): Pair<List<DoctorAvailability>, List<Leave>>
    suspend fun getPaymentReference(mobileNumber: String, amount: String, patientId: String, patientName: String, doctorName: String): PaymentRequest?
    suspend fun getPaymentStatus(merchantTransactionId: String): PaymentStatus
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
    ): InsertOpdResponse
}