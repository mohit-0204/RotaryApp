@file:OptIn(ExperimentalTime::class)

package com.rotary.hospital.feature.patient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.domain.*
import com.rotary.hospital.feature.patient.data.model.ApiPatient
import com.rotary.hospital.feature.patient.domain.usecase.RegisterPatientUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.error_address
import rotaryhospital.composeapp.generated.resources.error_blood_group
import rotaryhospital.composeapp.generated.resources.error_city
import rotaryhospital.composeapp.generated.resources.error_dob
import rotaryhospital.composeapp.generated.resources.error_email
import rotaryhospital.composeapp.generated.resources.error_full_name
import rotaryhospital.composeapp.generated.resources.error_guardian_name
import rotaryhospital.composeapp.generated.resources.error_no_internet
import rotaryhospital.composeapp.generated.resources.error_no_patient_data
import rotaryhospital.composeapp.generated.resources.error_profile_not_found
import rotaryhospital.composeapp.generated.resources.error_registration_failed
import rotaryhospital.composeapp.generated.resources.error_server
import rotaryhospital.composeapp.generated.resources.error_state
import rotaryhospital.composeapp.generated.resources.error_timeout
import rotaryhospital.composeapp.generated.resources.error_unknown
import rotaryhospital.composeapp.generated.resources.error_update_failed
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientRegistrationViewModel.GuardianType


fun calculateAge(dob: String): String? {
    // Parse "dd-mm-yyyy" format
    return try {
        val parts = dob.split("-")
        if (parts.size != 3) return null
        val day = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val year = parts[2].toIntOrNull() ?: return null
        val birthDate = LocalDate(year, month, day)
        val today =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        var age = today.year - birthDate.year
        if (today.month < birthDate.month || (today.month == birthDate.month && today.day < birthDate.day)) {
            age--
        }
        age.toString()
    } catch (e: Exception) {
        Logger.e("DOB SELECTOR", "Error calculating age: ${e.message}")
        null
    }
}

// NOTE: This enum is no longer needed as the event system handles display type implicitly.
// enum class ErrorDisplayType {
//     SNACKBAR,
//     DIALOG
// }

// NEW: Sealed class for one-shot events sent to the UI.
sealed class RegistrationEvent {
    data class NavigateOnSuccess(val patientName: String) : RegistrationEvent()
    data class ShowSnackbar(val message: UiText) : RegistrationEvent()
    data class ShowDialog(val message: UiText) : RegistrationEvent()
}

sealed class PatientRegistrationState {
    object Idle : PatientRegistrationState()
    object Loading : PatientRegistrationState()
    data class Success(val patient: ApiPatient) : PatientRegistrationState()
    // NOTE: The Error state is removed as transient errors are now handled by the event channel.
    // data class Error(val message: UiText, val displayType: ErrorDisplayType) : PatientRegistrationState()
}

data class RegistrationFormState(
    val mobileNumber: String = "",
    val fullName: String = "",
    val gender: Gender = Gender.Male,
    val dob: String = "",
    val bloodGroup: String = "",
    val guardianName: String = "",
//    val relation: Relation = Relation.SonOf,
    // 👇 CHANGE THIS LINE
    val guardianType: GuardianType = GuardianType.Father,
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val fieldErrors: Map<String, UiText.StringResource> = emptyMap(),
    // This property holds the key of the first field with an error, to trigger a scroll in the UI.
    val firstErrorField: String? = null
)

enum class Gender(val label: String) {
    Male("Male"), Female("Female"), Other("Other")
}

enum class Relation {
    SonOf, DaughterOf, WifeOf;

    fun toApiString(): String = when (this) {
        SonOf -> "S/O"
        DaughterOf -> "D/O"
        WifeOf -> "W/O"
    }
}

class PatientRegistrationViewModel(
    private val registerPatientUseCase: RegisterPatientUseCase,
    private val preferences: PreferencesManager
) : ViewModel() {
    private val _state = MutableStateFlow<PatientRegistrationState>(PatientRegistrationState.Idle)
    val state: StateFlow<PatientRegistrationState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()

    // Channel for sending one-shot events to the UI.
    private val _eventChannel = Channel<RegistrationEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(
                mobileNumber = preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").first()
            )
        }
    }

    fun updateFormState(formState: RegistrationFormState) {
        // When user types, clear previous validation errors and scroll triggers.
        _formState.value = formState.copy(fieldErrors = emptyMap(), firstErrorField = null)
    }

    // A function to reset the scroll trigger after the UI has handled it.
    fun clearScrollToError() {
        _formState.value = _formState.value.copy(firstErrorField = null)
    }

    // NOTE: This function is no longer needed as the event-driven UI will consume the event once.
    // fun errorHandled() {
    //     _state.value = PatientRegistrationState.Idle
    // }

    fun registerPatient() {
        val form = _formState.value
        val validationErrors = validateInputs(
            form.fullName, form.guardianName, form.dob, form.bloodGroup,
            form.email, form.address, form.city, form.state
        )
        if (validationErrors != null) {
            // If validation fails, we now determine the *first* field with an error
            // to notify the UI which field to scroll to.
            val fieldOrder = listOf("fullName", "dob", "bloodGroup", "guardianName", "email", "address", "city", "state")
            val firstErrorKey = fieldOrder.firstOrNull { it in validationErrors }

            _formState.value = form.copy(
                fieldErrors = validationErrors,
                firstErrorField = firstErrorKey
            )
            return
        }

        viewModelScope.launch {
            _state.value = PatientRegistrationState.Loading
            when (val result = registerPatientUseCase(
                mobileNumber = form.mobileNumber,
                name = form.fullName,
//                guardianType = form.relation.toApiString(),
                // 👇 CHANGE THIS LINE
                guardianType = getGuardianApiString(form.guardianType),
                guardianName = form.guardianName,
                gender = form.gender.label,
                age = form.dob,
                bloodGroup = form.bloodGroup,
                email = form.email,
                address = form.address,
                city = form.city,
                state = form.state
            )) {
                is Result.Success -> {
                    val patient = result.data.data?.firstOrNull()
                    if (patient != null) {
                        preferences.saveBoolean(PreferenceKeys.IS_LOGGED_IN, true)
                        preferences.saveString(PreferenceKeys.PATIENT_ID, patient.id)
                        preferences.saveString(PreferenceKeys.MOBILE_NUMBER, form.mobileNumber)
                        preferences.saveString(PreferenceKeys.PATIENT_NAME, patient.name)
                        _state.value = PatientRegistrationState.Success(patient)
                    }  else {
                        _state.value = PatientRegistrationState.Idle // Reset loading state
                        _eventChannel.send(RegistrationEvent.ShowDialog(UiText.StringResource(Res.string.error_unknown)))
                    }
                }
                is Result.Error -> {
                    _state.value = PatientRegistrationState.Idle // Reset loading state
                    mapErrorToEvent(result.error) // Send error event
                }
            }
        }
    }

    private fun validateInputs(
        fullName: String,
        guardianName: String,
        dob: String,
        bloodGroup: String,
        email: String,
        address: String,
        city: String,
        state: String
    ): MutableMap<String, UiText.StringResource>? {
        val errors = mutableMapOf<String, UiText.StringResource>()
        if (fullName.isBlank()) errors["fullName"] =
            UiText.StringResource(Res.string.error_full_name)
        if (guardianName.isBlank()) errors["guardianName"] =
            UiText.StringResource(Res.string.error_guardian_name)
        if (dob.isBlank()) errors["dob"] = UiText.StringResource(Res.string.error_dob)
        if (bloodGroup.isBlank()) errors["bloodGroup"] =
            UiText.StringResource(Res.string.error_blood_group)
        if (email.isBlank() || !isValidEmail(email)) errors["email"] =
            UiText.StringResource(Res.string.error_email)
        if (address.isBlank()) errors["address"] = UiText.StringResource(Res.string.error_address)
        if (city.isBlank()) errors["city"] = UiText.StringResource(Res.string.error_city)
        if (state.isBlank()) errors["state"] = UiText.StringResource(Res.string.error_state)
        return errors.ifEmpty { null }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" +
                    "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
                    "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
                    "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
                    "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" +
                    "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        )
        return emailRegex.matches(email)
    }

    // NEW: This function maps an AppError to a RegistrationEvent and sends it through the channel.
    private suspend fun mapErrorToEvent(error: AppError) {
        val event: RegistrationEvent = when (error) {
            // Transient errors that are good for a Snackbar
            is NetworkError.NoInternet -> RegistrationEvent.ShowSnackbar(
                UiText.StringResource(Res.string.error_no_internet)
            )
            is NetworkError.Timeout -> RegistrationEvent.ShowSnackbar(
                UiText.StringResource(Res.string.error_timeout)
            )

            // Critical or detailed errors that need a Dialog
            is PatientError.RegistrationFailed -> RegistrationEvent.ShowDialog(
                UiText.StringResource(Res.string.error_registration_failed)
            )
            is PatientError.UpdateFailed -> RegistrationEvent.ShowDialog(
                UiText.StringResource(Res.string.error_update_failed)
            )
            is PatientError.NoPatientsFound -> RegistrationEvent.ShowDialog(
                UiText.StringResource(Res.string.error_no_patient_data)
            )
            is PatientError.ProfileNotFound -> RegistrationEvent.ShowDialog(
                UiText.StringResource(Res.string.error_profile_not_found)
            )
            is PatientError.ServerMessage -> RegistrationEvent.ShowDialog(
                UiText.DynamicString(error.message) // This could be a long server message
            )
            is ServerError -> RegistrationEvent.ShowDialog(
                UiText.StringResource(Res.string.error_server)
            )

            // Default to a dialog for safety
            else -> RegistrationEvent.ShowDialog(
                UiText.StringResource(Res.string.error_unknown)
            )
        }
        _eventChannel.send(event)
    }


    /* ---+++---+++---+++---+++---+++---+++---+++--- */
    // This is the new state enum you should use.
    enum class GuardianType {
        Father, Husband
    }

    // added for new guardian type etc
    private fun getGuardianApiString(guardianType: GuardianType): String {
        return when (guardianType) {
            GuardianType.Father -> "S/O"
            GuardianType.Husband -> "W/O" // Wife Of
        }
    }
}