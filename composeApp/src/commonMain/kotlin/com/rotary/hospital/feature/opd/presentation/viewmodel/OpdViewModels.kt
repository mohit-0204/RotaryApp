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
import kotlinx.coroutines.flow.update
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

    private val _state = MutableStateFlow(RegisterNewOpdUiState())
    val state: StateFlow<RegisterNewOpdUiState> = _state.asStateFlow()

    fun fetchSpecializations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getSpecializationsUseCase().fold(
                onSuccess = { specs ->
                    _state.update { it.copy(isLoading = false, specializations = specs) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun fetchDoctors(specialization: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getDoctorsUseCase(specialization).fold(
                onSuccess = { doctors ->
                    _state.update { it.copy(isLoading = false, doctors = doctors) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun fetchSlots(doctorId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getSlotsUseCase(doctorId).fold(
                onSuccess = { slots ->
                    _state.update { it.copy(isLoading = false, slots = slots) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun fetchAvailability(doctorId: String, slotId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getAvailabilityUseCase(doctorId, slotId).fold(
                onSuccess = { availability ->
                    _state.update { it.copy(isLoading = false, availability = availability) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
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
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getPaymentReferenceUseCase(mobileNumber, amount, patientId, patientName, doctorName)
                .fold(
                    onSuccess = { payment ->
                        _state.update { it.copy(isLoading = false, payment = payment) }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, errorMessage = error.message) }
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
            _state.update { it.copy(isLoading = true, errorMessage = null) }
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
                onSuccess = { response ->
                    _state.update { it.copy(isLoading = false, response = response) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun clearResponse() {
        _state.update { it.copy(response = null, payment = null) }
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
data class RegisterNewOpdUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val specializations: List<Specialization> = emptyList(),
    val doctors: List<Doctor> = emptyList(),
    val slots: List<Slot> = emptyList(),
    val availability: Availability? = null,
    val payment: PaymentRequest? = null,
    val response: InsertOpdResponse? = null
)


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