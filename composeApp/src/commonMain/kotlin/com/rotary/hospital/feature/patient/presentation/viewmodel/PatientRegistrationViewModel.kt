package com.rotary.hospital.feature.patient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.feature.patient.data.model.ApiPatient
import com.rotary.hospital.feature.patient.domain.usecase.RegisterPatientUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class PatientRegistrationState {
    object Idle : PatientRegistrationState()
    object Loading : PatientRegistrationState()
    data class Success(val patient: ApiPatient) : PatientRegistrationState()
    data class Error(val message: String) : PatientRegistrationState()
}

data class RegistrationFormState(
    val mobileNumber : String = "",
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
    val fieldErrors: Map<String, String> = emptyMap()
)

enum class Gender(val label: String) {
    Male("Male"), Female("Female"), Other("Other")
}

enum class Relation(val label: String) {
    SonOf("Son of"), DaughterOf("Daughter of"), WifeOf("Wife of"), Other("Other");

    fun toApiString(): String = when (this) {
        SonOf -> "S/O"
        DaughterOf -> "D/O"
        WifeOf -> "W/O"
        Other -> "Other"
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
        _formState.value = formState.copy(fieldErrors = emptyMap())
    }

    fun registerPatient() {
        val form = _formState.value
        val validationErrors = validateInputs(
            form.fullName, form.guardianName, form.dob, form.bloodGroup,
            form.email, form.address, form.city, form.state
        )
        if (validationErrors != null) {
            _formState.value = form.copy(fieldErrors = validationErrors)
            return
        }

        viewModelScope.launch {
            _state.value = PatientRegistrationState.Loading
            val result = registerPatientUseCase(
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
            )
            _state.value = when {
                result.isSuccess -> {
                    val response = result.getOrNull()!!
                    val patient = response.data?.firstOrNull()
                    if (patient != null) {
                        preferences.saveString(PreferenceKeys.PATIENT_ID, patient.id)
                        preferences.saveString(PreferenceKeys.MOBILE_NUMBER, form.mobileNumber)
                        preferences.saveString(PreferenceKeys.PATIENT_NAME, patient.name)
                        preferences.saveBoolean(PreferenceKeys.IS_LOGGED_IN, true)
                        PatientRegistrationState.Success(patient)
                    } else {
                        PatientRegistrationState.Error("No patient data received")
                    }
                }
                else -> PatientRegistrationState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
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
    ): Map<String, String>? {
        val errors = mutableMapOf<String, String>()
        if (fullName.isEmpty()) errors["fullName"] = "Please enter a valid name"
        if (guardianName.isEmpty()) errors["guardianName"] = "Please enter a valid guardian name"
        if (dob.isEmpty()) errors["dob"] = "Please enter a valid date of birth"
        if (bloodGroup.isEmpty()) errors["bloodGroup"] = "Please select a blood group"
        if (email.isEmpty() || !isValidEmail(email)) errors["email"] = "Please enter a valid email address"
        if (address.isEmpty()) errors["address"] = "Please enter a valid address"
        if (city.isEmpty()) errors["city"] = "Please enter a valid city"
        if (state.isEmpty()) errors["state"] = "Please enter a valid state"
        return if (errors.isEmpty()) null else errors
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
}