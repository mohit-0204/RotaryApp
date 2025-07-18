package com.rotary.hospital.feature.opd.data.mapper

import com.rotary.hospital.feature.opd.data.remote.dto.*
import com.rotary.hospital.feature.opd.domain.model.*

fun OpdDto.toDomain(): Opd {
    return Opd(
        opdId = opdId,
        opdType = opdType,
        patientId = patientId,
        patientName = patientName,
        doctor = doctor,
        date = date
    )
}


fun PatientDto.toDomain(): Patient = Patient(
    patientId = patientId ?: throw IllegalArgumentException("Patient ID missing"),
    patientName = patientName ?: "Unknown"
)

fun SpecializationDto.toDomain(): Specialization = Specialization(
    data = data ?: "Unknown"
)

fun DoctorDto.toDomain(): Doctor = Doctor(
    name = name ?: "Unknown",
    id = id ?: throw IllegalArgumentException("Doctor ID missing"),
    opdRoom = opdRoom ?: "Unknown"
)

fun SlotDto.toDomain(): Slot = Slot(
    timeFrom = timeFrom ?: "Unknown",
    timeTo = timeTo ?: "Unknown"
)

fun AvailabilityDto.toDomain(): Availability = Availability(
    docCharges = docCharges.toString(),
    docOnlineCharges = docOnlineCharges ?: "Unknown",
    docTimeFrom = docTimeFrom ?: "Unknown",
    docTimeTo = docTimeTo ?: "Unknown",
    approximateTime = approximateTime ?: "Unknown",
    docDurationPerPatient = docDurationPerPatient ?: "Unknown",
    docNoOfAppointments = docNoOfAppointments ?: "Unknown",
    appointments = appointments ?: "Unknown"
)

fun DoctorAvailabilityDto.toDomain(): DoctorAvailability = DoctorAvailability(
    docFrequency = docFrequency ?: "Unknown",
    docDays = docDays ?: "Unknown",
    docTimeFrom = docTimeFrom ?: "Unknown",
    docTimeTo = docTimeTo ?: "Unknown"
)

fun LeaveDto.toDomain(): Leave = Leave(
    cancelDate = cancelDate ?: "Unknown",
    cancelDateTo = cancelDateTo ?: "Unknown"
)

fun PaymentRequestDto.toDomain(): PaymentRequest = PaymentRequest(
    apiEndPoint = apiEndPoint ?: throw IllegalArgumentException("API endpoint missing"),
    payloadBase64 = payloadBase64 ?: throw IllegalArgumentException("Payload missing"),
    checksum = checksum ?: throw IllegalArgumentException("Checksum missing"),
    merchantTransactionId = merchantTransactionId ?: throw IllegalArgumentException("Transaction ID missing")
)

fun PaymentStatusDto.toDomain(): PaymentStatus = PaymentStatus(
    response = response ?: "Unknown",
    messageCode = messageCode ?: "Unknown",
    message = message ?: "Unknown",
    transactionId = transactionId ?: throw IllegalArgumentException("Transaction ID missing")
)

fun InsertOpdResponseDto.toDomain(): InsertOpdResponse = InsertOpdResponse(
    response = response ?: false,
    message = message ?: "Unknown",
    opdId = opdId.toString(),
    opdDate = opdDate,
    tokenNumber = tokenNumber.toString(),
    estimatedTime = estimatedTime
)
