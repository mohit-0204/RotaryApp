package com.rotary.hospital.feature.patient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.feature.patient.data.model.ApiPatientProfile
import com.rotary.hospital.feature.patient.domain.usecase.GetPatientProfileUseCase
import com.rotary.hospital.feature.patient.domain.usecase.UpdatePatientProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class PatientProfileViewModel(
    private val getPatientProfileUseCase: GetPatientProfileUseCase,
    private val updatePatientProfileUseCase: UpdatePatientProfileUseCase,
    private val preferences: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow<PatientProfileState>(PatientProfileState.Idle)
    val state: StateFlow<PatientProfileState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(PatientProfileFormState())
    val formState: StateFlow<PatientProfileFormState> = _formState.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    init {
        fetchPatientProfile()
    }

    fun fetchPatientProfile() {
        viewModelScope.launch {
            val patientId = preferences.getString(PreferenceKeys.PATIENT_ID, "").first()
            if (patientId.isEmpty()) {
                _state.value = PatientProfileState.Error("No patient ID found")
                return@launch
            }
            _state.value = PatientProfileState.Loading
            val result = getPatientProfileUseCase(patientId)
            _state.value = when {
                result.isSuccess -> {
                    val profile = result.getOrNull()!!
                    _formState.value = PatientProfileFormState(
                        patientId = patientId,
                        mobileNumber = preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").first(),
                        fullName = profile.name,
                        gender = Gender.entries.find { it.label == profile.gender } ?: Gender.Male,
                        dob = profile.age,
                        bloodGroup = profile.bloodGroup,
                        guardianName = profile.guardianName,
                        relation = Relation.entries.find { it.toApiString() == profile.gender } ?: Relation.SonOf,
                        email = profile.email,
                        address = profile.address,
                        city = profile.city,
                        state = profile.state
                    )
                    PatientProfileState.Success(profile)
                }
                else -> PatientProfileState.Error(result.exceptionOrNull()?.message ?: "Error fetching profile")
            }
        }
    }

    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
        if (!_isEditing.value) {
            // Reset form state to last fetched data when exiting edit mode
            fetchPatientProfile()
        }
    }

    fun updateFormState(formState: PatientProfileFormState) {
        _formState.value = formState
    }

    fun updatePatientProfile() {
        viewModelScope.launch {
            val form = _formState.value
            val errors = validateInputs(
                fullName = form.fullName,
                guardianName = form.guardianName,
                dob = form.dob,
                bloodGroup = form.bloodGroup,
                email = form.email,
                address = form.address,
                city = form.city,
                state = form.state
            )
            if (errors != null) {
                _formState.value = form.copy(fieldErrors = errors)
                _state.value = PatientProfileState.Error("Please fix form errors")
                return@launch
            }

            _state.value = PatientProfileState.Loading
            val result = updatePatientProfileUseCase(
                mobileNumber = form.mobileNumber,
                patientId = form.patientId,
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
                result.isSuccess && result.getOrNull() == true -> {
                    preferences.saveString(PreferenceKeys.PATIENT_NAME, form.fullName)
                    _isEditing.value = false // Exit edit mode on success
                    PatientProfileState.Success(ApiPatientProfile(
                        name = form.fullName,
                        guardianName = form.guardianName,
                        age = form.dob,
                        address = form.address,
                        email = form.email,
                        city = form.city,
                        state = form.state,
                        bloodGroup = form.bloodGroup,
                        gender = form.gender.label
                    ))
                }
                else -> PatientProfileState.Error(result.exceptionOrNull()?.message ?: "Update failed")
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
}

sealed class PatientProfileState {
    object Idle : PatientProfileState()
    object Loading : PatientProfileState()
    data class Success(val profile: ApiPatientProfile) : PatientProfileState()
    data class Error(val message: String) : PatientProfileState()
}

data class PatientProfileFormState(
    val patientId: String = "",
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
    val fieldErrors: Map<String, String> = emptyMap()
)