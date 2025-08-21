package com.rotary.hospital.feature.opd.data.mapper

import com.rotary.hospital.feature.opd.data.remote.dto.*
import com.rotary.hospital.feature.opd.domain.model.*

fun OpdDto.toDomain(): Result<Opd> {
    return try {
        Result.success(
            Opd(
                opdId = opdId,
                opdType = opdType,
                patientId = patientId,
                patientName = patientName,
                doctor = doctor,
                date = date
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun OpdDetailsDto.toDomain(): Result<OpdDetails> {
    return try {
        Result.success(
            OpdDetails(
                opdId = opdId,
                opdDate = opdDate,
                tokenNumber = tokenNumber,
                estimatedTime = estimatedTime,
                opdCharges = opdCharges,
                patientId = patientId,
                patientName = patientName,
                doctor = doctor,
                transactionId = transactionId,
                orderId = orderId,
                paymentId = paymentId,
                transactionStatus = transactionStatus,
                transactionMessage = transactionMessage
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun PatientDto.toDomain(): Result<Patient> {
    return try {
        Result.success(
            Patient(
                patientId = patientId ?: "",
                patientName = patientName ?: ""
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun SpecializationDto.toDomain(): Result<Specialization> {
    return try {
        Result.success(
            Specialization(
                data = data ?: ""
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun DoctorDto.toDomain(): Result<Doctor> {
    return try {
        Result.success(
            Doctor(
                name = name ?: "",
                id = id ?: "",
                opdRoom = opdRoom ?: ""
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun SlotDto.toDomain(): Result<Slot> {
    return try {
        Result.success(
            Slot(
                timeFrom = timeFrom ?: "",
                timeTo = timeTo ?: ""
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun AvailabilityDto.toDomain(): Result<Availability> {
    return try {
        Result.success(
            Availability(
                docCharges = docCharges,
                docOnlineCharges = docOnlineCharges ?: "",
                docTimeFrom = docTimeFrom ?: "",
                docTimeTo = docTimeTo ?: "",
                approximateTime = approximateTime ?: "",
                docDurationPerPatient = docDurationPerPatient ?: "",
                docNoOfAppointments = docNoOfAppointments ?: "",
                appointments = appointments ?: ""
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun DoctorAvailabilityDto.toDomain(): Result<DoctorAvailability> {
    return try {
        Result.success(
            DoctorAvailability(
                docFrequency = docFrequency ?: "",
                docDays = docDays ?: "",
                docTimeFrom = docTimeFrom ?: "",
                docTimeTo = docTimeTo ?: ""
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun LeaveDto.toDomain(): Result<Leave> {
    return try {
        Result.success(
            Leave(
                cancelDate = cancelDate ?: "",
                cancelDateTo = cancelDateTo ?: ""
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}