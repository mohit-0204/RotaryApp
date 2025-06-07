package com.rotary.hospital.feature.opd.data.repository

import com.rotary.hospital.feature.opd.data.network.OpdService
import com.rotary.hospital.feature.opd.data.model.*
import com.rotary.hospital.feature.opd.domain.repository.OpdRepository

class OpdRepositoryImpl(private val service: OpdService) : OpdRepository {
    override suspend fun getBookedOpds(mobileNumber: String): List<Opd> {
        return service.getBookedOpds(mobileNumber)
    }

    override suspend fun getRegisteredPatients(mobileNumber: String): List<Patient> {
        return service.getRegisteredPatients(mobileNumber)
    }

    override suspend fun getSpecializations(): List<Specialization> {
        return service.getSpecializations()
    }

    override suspend fun getDoctors(specialization: String): List<Doctor> {
        return service.getDoctors(specialization)
    }

    override suspend fun getSlots(doctorId: String): List<Slot> {
        return service.getSlots(doctorId)
    }

    override suspend fun getAvailability(doctorId: String, slotId: String): Availability? {
        return service.getAvailability(doctorId, slotId)
    }

    override suspend fun getDoctorAvailability(doctorId: String): Pair<List<DoctorAvailability>, List<Leave>> {
        return service.getDoctorAvailability(doctorId)
    }

    override suspend fun getPaymentReference(mobileNumber: String, amount: String, patientId: String, patientName: String, doctorName: String): PaymentRequest? {
        return service.getPaymentReference(mobileNumber, amount, patientId, patientName, doctorName)
    }

    override suspend fun getPaymentStatus(merchantTransactionId: String): PaymentStatus {
        return service.getPaymentStatus(merchantTransactionId)
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
    ): InsertOpdResponse {
        return service.insertOpd(
            patientId, patientName, mobileNumber, doctorName, doctorId, opdAmount,
            durationPerPatient, docTimeFrom, opdType, transactionId, paymentId, orderId, status, message
        )
    }
}