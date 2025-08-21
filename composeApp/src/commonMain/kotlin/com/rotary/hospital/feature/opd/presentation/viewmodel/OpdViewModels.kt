package com.rotary.hospital.feature.opd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.feature.opd.domain.model.Availability
import com.rotary.hospital.feature.opd.domain.model.Doctor
import com.rotary.hospital.feature.opd.domain.model.DoctorAvailability
import com.rotary.hospital.feature.opd.domain.model.InsertOpdResponse
import com.rotary.hospital.feature.opd.domain.model.Leave
import com.rotary.hospital.feature.opd.domain.model.Opd
import com.rotary.hospital.feature.opd.domain.model.Patient
import com.rotary.hospital.feature.opd.domain.model.PaymentStatus
import com.rotary.hospital.feature.opd.domain.model.Slot
import com.rotary.hospital.feature.opd.domain.model.Specialization
import com.rotary.hospital.feature.opd.domain.usecase.*
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterNewOpdViewModel(
    private val getRegisteredPatientsUseCase: GetRegisteredPatientsUseCase,
    private val getSpecializationsUseCase: GetSpecializationsUseCase,
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val getSlotsUseCase: GetSlotsUseCase,
    private val getAvailabilityUseCase: GetAvailabilityUseCase,
    private val initiatePaymentFlowUseCase: InitiatePaymentFlowUseCase
) : ViewModel() {
    private val _paymentState = MutableStateFlow<UiState<InsertOpdResponse>>(UiState.Idle)
    val paymentState: StateFlow<UiState<InsertOpdResponse>> = _paymentState.asStateFlow()

    private val _availabilityState = MutableStateFlow<UiState<Availability>>(UiState.Idle)
    val availabilityState: StateFlow<UiState<Availability>> = _availabilityState.asStateFlow()

    private val _patientsState = MutableStateFlow<UiState<List<Patient>>>(UiState.Idle)
    val patientsState: StateFlow<UiState<List<Patient>>> = _patientsState.asStateFlow()

    private val _specializationsState = MutableStateFlow<UiState<List<Specialization>>>(UiState.Idle)
    val specializationsState: StateFlow<UiState<List<Specialization>>> = _specializationsState.asStateFlow()

    private val _doctorsState = MutableStateFlow<UiState<List<Doctor>>>(UiState.Idle)
    val doctorsState: StateFlow<UiState<List<Doctor>>> = _doctorsState.asStateFlow()

    private val _slotsState = MutableStateFlow<UiState<List<Slot>>>(UiState.Idle)
    val slotsState: StateFlow<UiState<List<Slot>>> = _slotsState.asStateFlow()

    // Additional state for selections (to preserve existing functionality)
    private val _selectedPatient = MutableStateFlow<Patient?>(null)
    val selectedPatient: StateFlow<Patient?> = _selectedPatient.asStateFlow()

    private val _selectedSpecialization = MutableStateFlow<String?>(null)
    val selectedSpecialization: StateFlow<String?> = _selectedSpecialization.asStateFlow()

    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> = _selectedDoctor.asStateFlow()

    private val _selectedSlot = MutableStateFlow<Slot?>(null)
    val selectedSlot: StateFlow<Slot?> = _selectedSlot.asStateFlow()

    fun fetchRegisteredPatients(mobileNumber: String) {
        viewModelScope.launch {
            _patientsState.value = UiState.Loading
            getRegisteredPatientsUseCase(mobileNumber).fold(
                onSuccess = { patients -> _patientsState.value = UiState.Success(patients) },
                onFailure = { error -> _patientsState.value = UiState.Error(error.message ?: "Failed to fetch patients") }
            )
        }
    }

    fun onSelectPatient(patient: Patient) {
        _selectedPatient.value = patient
    }

    fun fetchSpecializations() {
        viewModelScope.launch {
            _specializationsState.value = UiState.Loading
            getSpecializationsUseCase().fold(
                onSuccess = { specs -> _specializationsState.value = UiState.Success(specs) },
                onFailure = { error -> _specializationsState.value = UiState.Error(error.message ?: "Failed to fetch specializations") }
            )
        }
    }

    fun onSelectSpecialization(specialization: String) {
        _selectedSpecialization.value = specialization
        fetchDoctors(specialization)
    }

    fun fetchDoctors(specialization: String) {
        viewModelScope.launch {
            _doctorsState.value = UiState.Loading
            getDoctorsUseCase(specialization).fold(
                onSuccess = { doctors -> _doctorsState.value = UiState.Success(doctors) },
                onFailure = { error -> _doctorsState.value = UiState.Error(error.message ?: "Failed to fetch doctors") }
            )
        }
    }

    fun onSelectDoctor(doctor: Doctor) {
        _selectedDoctor.value = doctor
        fetchSlots(doctor.id)
    }

    fun fetchSlots(doctorId: String) {
        viewModelScope.launch {
            _slotsState.value = UiState.Loading
            getSlotsUseCase(doctorId).fold(
                onSuccess = { slots -> _slotsState.value = UiState.Success(slots) },
                onFailure = { error -> _slotsState.value = UiState.Error(error.message ?: "Failed to fetch slots") }
            )
        }
    }

    fun onSelectSlot(slot: Slot) {
        _selectedSlot.value = slot
        _selectedDoctor.value?.id?.let { docId ->
            fetchAvailability(docId, slot.timeFrom)
        }
    }

    fun fetchAvailability(doctorId: String, slotId: String) {
        viewModelScope.launch {
            _availabilityState.value = UiState.Loading
            getAvailabilityUseCase(doctorId, slotId).fold(
                onSuccess = { availability ->
                    _availabilityState.value = availability?.let { UiState.Success(it) } ?: UiState.Error("No availability found")
                },
                onFailure = { error -> _availabilityState.value = UiState.Error(error.message ?: "Failed to fetch availability") }
            )
        }
    }

    fun initiatePayment(
        paymentHandler: PaymentHandler, // Pass from screen
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String,
        doctorId: String,
        durationPerPatient: String,
        docTimeFrom: String,
        opdType: String
    ) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            initiatePaymentFlowUseCase(
                paymentHandler,
                mobileNumber, amount, patientId, patientName, doctorName,
                doctorId, durationPerPatient, docTimeFrom, opdType
            ).collectLatest { result ->
                when (result) {
                    is PaymentFlowResult.Loading -> _paymentState.value = UiState.Loading
                    is PaymentFlowResult.Success -> _paymentState.value = UiState.Success(result.response)
                    is PaymentFlowResult.Pending -> _paymentState.value = UiState.Error("Payment pending, please check status")
                    is PaymentFlowResult.Error -> _paymentState.value = UiState.Error(result.message)
                }
            }
        }
    }
}
class OpdPaymentSuccessViewModel(
    private val getPaymentStatusUseCase: GetPaymentStatusUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<PaymentStatus>>(UiState.Idle)
    val state: StateFlow<UiState<PaymentStatus>> = _state.asStateFlow()

    fun checkPaymentStatus(merchantTransactionId: String, maxAttempts: Int = 5, pollIntervalMs: Long = 5000) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repeat(maxAttempts) { attempt ->
                getPaymentStatusUseCase(merchantTransactionId).fold(
                    onSuccess = { status ->
                        if (status.isSuccess) {
                            _state.value = UiState.Success(status)
                            return@launch
                        } else if (status.isPending) {
                            _state.value = UiState.Error("Payment still pending (attempt ${attempt + 1}/$maxAttempts)")
                            delay(pollIntervalMs)
                        } else {
                            _state.value = UiState.Error("Payment failed: ${status.message}")
                            return@launch
                        }
                    },
                    onFailure = { error ->
                        _state.value = UiState.Error("Status check failed: ${error.message}")
                        return@launch
                    }
                )
            }
            if (_state.value !is UiState.Success) {
                _state.value = UiState.Error("Payment status check timed out after $maxAttempts attempts")
            }
        }
    }
}

class OpdPaymentPendingViewModel : ViewModel() {
    data class PendingState(
        val message: String,
        val retryCount: Int = 0,
        val lastUpdated: Long = getTimeMillis()
    )

    private val _state = MutableStateFlow<UiState<PendingState>>(UiState.Idle)
    val state: StateFlow<UiState<PendingState>> = _state.asStateFlow()

    fun updatePendingState(message: String, retryCount: Int) {
        _state.value = UiState.Success(PendingState(message, retryCount))
    }
}

class OpdPaymentFailedViewModel : ViewModel() {
    data class FailedState(
        val message: String,
        val canRetry: Boolean = true,
        val lastUpdated: Long = getTimeMillis()
    )

    private val _state = MutableStateFlow<UiState<FailedState>>(UiState.Idle)
    val state: StateFlow<UiState<FailedState>> = _state.asStateFlow()

    fun updateFailedState(message: String, canRetry: Boolean = true) {
        _state.value = UiState.Success(FailedState(message, canRetry))
    }
}

class DoctorAvailabilityViewModel(
    private val getDoctorAvailabilityUseCase: GetDoctorAvailabilityUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<Pair<List<DoctorAvailability>, List<Leave>>>>(UiState.Idle)
    val state: StateFlow<UiState<Pair<List<DoctorAvailability>, List<Leave>>>> = _state.asStateFlow()

    fun fetchDoctorAvailability(doctorId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            getDoctorAvailabilityUseCase(doctorId).fold(
                onSuccess = { data -> _state.value = UiState.Success(data) },
                onFailure = { error -> _state.value = UiState.Error(error.message ?: "Failed to fetch availability") }
            )
        }
    }
}
