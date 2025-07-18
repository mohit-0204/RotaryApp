package com.rotary.hospital.feature.opd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.feature.opd.domain.model.Availability
import com.rotary.hospital.feature.opd.domain.model.Doctor
import com.rotary.hospital.feature.opd.domain.model.DoctorAvailability
import com.rotary.hospital.feature.opd.domain.model.InsertOpdResponse
import com.rotary.hospital.feature.opd.domain.model.Leave
import com.rotary.hospital.feature.opd.domain.model.Opd
import com.rotary.hospital.feature.opd.domain.model.Patient
import com.rotary.hospital.feature.opd.domain.model.PaymentRequest
import com.rotary.hospital.feature.opd.domain.model.PaymentStatus
import com.rotary.hospital.feature.opd.domain.model.Slot
import com.rotary.hospital.feature.opd.domain.model.Specialization
import com.rotary.hospital.feature.opd.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisteredOpdsViewModel(
    private val getBookedOpdsUseCase: GetBookedOpdsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<RegisteredOpdsState>(RegisteredOpdsState.Idle)
    val state: StateFlow<RegisteredOpdsState> = _state.asStateFlow()

    fun fetchOpds(mobileNumber: String) {
        viewModelScope.launch {
            _state.value = RegisteredOpdsState.Loading
            getBookedOpdsUseCase(mobileNumber).fold(
                onSuccess = { opds -> _state.value = RegisteredOpdsState.Success(opds) },
                onFailure = { error ->
                    _state.value = RegisteredOpdsState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed interface RegisteredOpdsState {
    data object Idle : RegisteredOpdsState
    data object Loading : RegisteredOpdsState
    data class Success(val opds: List<Opd>) : RegisteredOpdsState
    data class Error(val message: String) : RegisteredOpdsState
}

class OpdPatientListViewModel(
    private val getRegisteredPatientsUseCase: GetRegisteredPatientsUseCase,
    private val getBookedOpdsUseCase: GetBookedOpdsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<OpdPatientListState>(OpdPatientListState.Idle)
    val state: StateFlow<OpdPatientListState> = _state.asStateFlow()

    fun fetchPatients(mobileNumber: String) {
        viewModelScope.launch {
            _state.value = OpdPatientListState.Loading
            getRegisteredPatientsUseCase(mobileNumber).fold(
                onSuccess = { patients -> _state.value = OpdPatientListState.Success(patients) },
                onFailure = { error ->
                    _state.value = OpdPatientListState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed interface OpdPatientListState {
    data object Idle : OpdPatientListState
    data object Loading : OpdPatientListState
    data class Success(val patients: List<Patient>) : OpdPatientListState
    data class Error(val message: String) : OpdPatientListState
}

class RegisterNewOpdViewModel(
    private val getSpecializationsUseCase: GetSpecializationsUseCase,
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val getSlotsUseCase: GetSlotsUseCase,
    private val getAvailabilityUseCase: GetAvailabilityUseCase,
    private val getPaymentReferenceUseCase: GetPaymentReferenceUseCase,
    private val insertOpdUseCase: InsertOpdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<RegisterNewOpdState>(RegisterNewOpdState.Idle)
    val state: StateFlow<RegisterNewOpdState> = _state.asStateFlow()

    var selectedAvailability: Availability? = null
        private set

    fun fetchSpecializations() {
        viewModelScope.launch {
            _state.value = RegisterNewOpdState.Loading
            getSpecializationsUseCase().fold(
                onSuccess = { specs ->
                    _state.value = RegisterNewOpdState.SpecializationsLoaded(specs)
                },
                onFailure = { error ->
                    _state.value = RegisterNewOpdState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun fetchDoctors(specialization: String) {
        viewModelScope.launch {
            _state.value = RegisterNewOpdState.Loading
            getDoctorsUseCase(specialization).fold(
                onSuccess = { doctors ->
                    _state.value = RegisterNewOpdState.DoctorsLoaded(doctors)
                },
                onFailure = { error ->
                    _state.value = RegisterNewOpdState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun fetchSlots(doctorId: String) {
        viewModelScope.launch {
            _state.value = RegisterNewOpdState.Loading
            getSlotsUseCase(doctorId).fold(
                onSuccess = { slots -> _state.value = RegisterNewOpdState.SlotsLoaded(slots) },
                onFailure = { error ->
                    _state.value = RegisterNewOpdState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun fetchAvailability(doctorId: String, slotId: String) {
        viewModelScope.launch {
            _state.value = RegisterNewOpdState.Loading
            getAvailabilityUseCase(doctorId, slotId).fold(
                onSuccess = { availability ->
                    selectedAvailability = availability
                    _state.value = RegisterNewOpdState.AvailabilityLoaded(availability)
                },
                onFailure = { error ->
                    _state.value = RegisterNewOpdState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun initiatePayment(
        mobileNumber: String,
        amount: String,
        patientId: String,
        patientName: String,
        doctorName: String
    ) {
        viewModelScope.launch {
            _state.value = RegisterNewOpdState.Loading
            getPaymentReferenceUseCase(
                mobileNumber,
                amount,
                patientId,
                patientName,
                doctorName
            ).fold(
                onSuccess = { payment ->
                    _state.value = RegisterNewOpdState.PaymentInitiated(payment)
                },
                onFailure = { error ->
                    _state.value = RegisterNewOpdState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun insertOpd(
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
    ) {
        viewModelScope.launch {
            _state.value = RegisterNewOpdState.Loading
            insertOpdUseCase(
                patientId,
                patientName,
                mobileNumber,
                doctorName,
                doctorId,
                opdAmount,
                durationPerPatient,
                docTimeFrom,
                opdType,
                transactionId,
                paymentId,
                orderId,
                status,
                message
            ).fold(
                onSuccess = { response -> _state.value = RegisterNewOpdState.Success(response) },
                onFailure = { error ->
                    _state.value = RegisterNewOpdState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed interface RegisterNewOpdState {
    data object Idle : RegisterNewOpdState
    data object Loading : RegisterNewOpdState
    data class SpecializationsLoaded(val specializations: List<Specialization>) :
        RegisterNewOpdState

    data class DoctorsLoaded(val doctors: List<Doctor>) : RegisterNewOpdState
    data class SlotsLoaded(val slots: List<Slot>) : RegisterNewOpdState
    data class AvailabilityLoaded(val availability: Availability?) : RegisterNewOpdState
    data class PaymentInitiated(val payment: PaymentRequest?) : RegisterNewOpdState
    data class Success(val response: InsertOpdResponse) : RegisterNewOpdState
    data class Error(val message: String) : RegisterNewOpdState
}

class DoctorAvailabilityViewModel(
    private val getDoctorAvailabilityUseCase: GetDoctorAvailabilityUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<DoctorAvailabilityState>(DoctorAvailabilityState.Idle)
    val state: StateFlow<DoctorAvailabilityState> = _state.asStateFlow()

    fun fetchAvailability(doctorId: String) {
        viewModelScope.launch {
            _state.value = DoctorAvailabilityState.Loading
            getDoctorAvailabilityUseCase(doctorId).fold(
                onSuccess = { (availability, leaves) ->
                    _state.value = DoctorAvailabilityState.Success(availability, leaves)
                },
                onFailure = { error ->
                    _state.value = DoctorAvailabilityState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed interface DoctorAvailabilityState {
    data object Idle : DoctorAvailabilityState
    data object Loading : DoctorAvailabilityState
    data class Success(val availability: List<DoctorAvailability>, val leaves: List<Leave>) :
        DoctorAvailabilityState

    data class Error(val message: String) : DoctorAvailabilityState
}

class OpdPaymentSuccessViewModel(
    private val getPaymentStatusUseCase: GetPaymentStatusUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<OpdPaymentSuccessState>(OpdPaymentSuccessState.Idle)
    val state: StateFlow<OpdPaymentSuccessState> = _state.asStateFlow()

    fun fetchPaymentStatus(merchantTransactionId: String) {
        viewModelScope.launch {
            _state.value = OpdPaymentSuccessState.Loading
            getPaymentStatusUseCase(merchantTransactionId).fold(
                onSuccess = { status ->
                    _state.value = when {
                        status.isSuccess -> OpdPaymentSuccessState.Success(status)
                        status.isPending -> OpdPaymentSuccessState.Pending(status)
                        else -> OpdPaymentSuccessState.Error("Payment failed: ${status.message}")
                    }
                },
                onFailure = { error ->
                    _state.value = OpdPaymentSuccessState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}


sealed interface OpdPaymentSuccessState {
    data object Idle : OpdPaymentSuccessState
    data object Loading : OpdPaymentSuccessState
    data class Success(val paymentStatus: PaymentStatus) : OpdPaymentSuccessState
    data class Pending(val paymentStatus: PaymentStatus) : OpdPaymentSuccessState
    data class Error(val message: String) : OpdPaymentSuccessState

}

class OpdPaymentPendingViewModel : ViewModel() {
    private val _state = MutableStateFlow<OpdPaymentPendingState>(OpdPaymentPendingState.Idle)
    val state: StateFlow<OpdPaymentPendingState> = _state.asStateFlow()

    fun updatePendingState(message: String) {
        _state.value = OpdPaymentPendingState.Pending(message)
    }
}

sealed interface OpdPaymentPendingState {
    data object Idle : OpdPaymentPendingState
    data class Pending(val message: String) : OpdPaymentPendingState
}

class OpdPaymentFailedViewModel : ViewModel() {
    private val _state = MutableStateFlow<OpdPaymentFailedState>(OpdPaymentFailedState.Idle)
    val state: StateFlow<OpdPaymentFailedState> = _state.asStateFlow()

    fun updateFailedState(message: String?) {
        _state.value = OpdPaymentFailedState.Failed(message ?: "Payment failed")
    }
}

sealed interface OpdPaymentFailedState {
    data object Idle : OpdPaymentFailedState
    data class Failed(val message: String) : OpdPaymentFailedState
}

class SelectedOpdDetailsViewModel(
    private val getBookedOpdsUseCase: GetBookedOpdsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<SelectedOpdDetailsState>(SelectedOpdDetailsState.Idle)
    val state: StateFlow<SelectedOpdDetailsState> = _state.asStateFlow()

    fun fetchOpdDetails(mobileNumber: String, opdId: String) {
        viewModelScope.launch {
            _state.value = SelectedOpdDetailsState.Loading
            getBookedOpdsUseCase(mobileNumber).fold(
                onSuccess = { opds ->
                    val opd = opds.find { it.opdId == opdId }
                    _state.value = if (opd != null) {
                        SelectedOpdDetailsState.Success(opd)
                    } else {
                        SelectedOpdDetailsState.Error("OPD not found")
                    }
                },
                onFailure = { error ->
                    _state.value = SelectedOpdDetailsState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed interface SelectedOpdDetailsState {
    data object Idle : SelectedOpdDetailsState
    data object Loading : SelectedOpdDetailsState
    data class Success(val opd: Opd) : SelectedOpdDetailsState
    data class Error(val message: String) : SelectedOpdDetailsState
}