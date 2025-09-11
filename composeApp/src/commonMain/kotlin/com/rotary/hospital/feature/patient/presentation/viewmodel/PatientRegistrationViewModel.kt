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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
import kotlin.time.ExperimentalTime


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
            kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
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

sealed class PatientRegistrationState {
    object Idle : PatientRegistrationState()
    object Loading : PatientRegistrationState()
    data class Success(val patient: ApiPatient) : PatientRegistrationState()
    data class Error(val message: UiText) : PatientRegistrationState()
}

data class RegistrationFormState(
    val mobileNumber: String = "",
    val fullName: String = "",
    val gender: Gender = Gender.Male,
    val dob: String = "",
    val bloodGroup: String = "",
    val guardianName: String = "",
    val relation: Relation = Relation.SonOf,
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

enum class Relation(val label: String) {
    SonOf("Son of"), DaughterOf("Daughter of"), WifeOf("Wife of");

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
                guardianType = form.relation.toApiString(),
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
                    } else {
                        _state.value = PatientRegistrationState.Error(
                            UiText.StringResource(Res.string.error_unknown)
                        )
                    }
                }

                is Result.Error -> {
                    _state.value = PatientRegistrationState.Error(mapErrorToUiText(result.error))
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

    private fun mapErrorToUiText(error: AppError): UiText {
        return when (error) {
            // Add specific patient errors
            is PatientError.RegistrationFailed -> UiText.StringResource(Res.string.error_registration_failed)
            is PatientError.UpdateFailed -> UiText.StringResource(Res.string.error_update_failed)
            is PatientError.NoPatientsFound -> UiText.StringResource(Res.string.error_no_patient_data)
            is PatientError.ProfileNotFound -> UiText.StringResource(Res.string.error_profile_not_found)
            is PatientError.ServerMessage -> UiText.DynamicString(error.message)
            // Existing Network errors
            is NetworkError.NoInternet -> UiText.StringResource(Res.string.error_no_internet)
            is NetworkError.Timeout -> UiText.StringResource(Res.string.error_timeout)
            is ServerError -> UiText.StringResource(Res.string.error_server)
            else -> UiText.StringResource(Res.string.error_unknown)
        }
    }
}