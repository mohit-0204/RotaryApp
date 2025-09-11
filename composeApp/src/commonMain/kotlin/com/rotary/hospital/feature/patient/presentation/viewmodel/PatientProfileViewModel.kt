package com.rotary.hospital.feature.patient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.domain.*
import com.rotary.hospital.feature.patient.data.model.ApiPatientProfile
import com.rotary.hospital.feature.patient.domain.usecase.GetPatientProfileUseCase
import com.rotary.hospital.feature.patient.domain.usecase.UpdatePatientProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.error_no_internet
import rotaryhospital.composeapp.generated.resources.error_server
import rotaryhospital.composeapp.generated.resources.error_timeout
import rotaryhospital.composeapp.generated.resources.error_unknown

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

    // todo : change guardianName to guardian type later
    fun fetchPatientProfile() {
        viewModelScope.launch {
            val patientId = preferences.getString(PreferenceKeys.PATIENT_ID, "").first()
            if (patientId.isEmpty()) {
                _state.value = PatientProfileState.Error(UiText.DynamicString("No patient ID found"))
                return@launch
            }
            _state.value = PatientProfileState.Loading
            when(val result = getPatientProfileUseCase(patientId)) {
                is Result.Success -> {
                    val profile = result.data.data.first()
                    _formState.value = PatientProfileFormState(
                        patientId = patientId,
                        mobileNumber = preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").first(),
                        fullName = profile.name,
                        gender = Gender.entries.find { it.label == profile.gender } ?: Gender.Male,
                        dob = profile.age,
                        bloodGroup = profile.bloodGroup,
                        guardianName = profile.guardianName,
                        relation = Relation.entries.find { it.toApiString() == profile.guardianName } ?: Relation.SonOf,
                        email = profile.email,
                        address = profile.address,
                        city = profile.city,
                        state = profile.state
                    )
                    _state.value = PatientProfileState.FetchSuccess(profile)
                }
                is Result.Error -> {
                    _state.value = PatientProfileState.Error(mapErrorToUiText(result.error))
                }
            }
        }
    }

    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
        if (!_isEditing.value) {
            // Reset form state to last fetched data when exiting edit mode
            (_state.value as? PatientProfileState.FetchSuccess)?.profile?.let { profile ->
                _formState.value = _formState.value.copy(
                    fullName = profile.name,
                    gender = Gender.entries.find { it.label == profile.gender } ?: Gender.Male,
                    dob = profile.age,
                    bloodGroup = profile.bloodGroup,
                    guardianName = profile.guardianName,
                    relation = Relation.entries.find { it.toApiString() == profile.guardianName } ?: Relation.SonOf,
                    email = profile.email,
                    address = profile.address,
                    city = profile.city,
                    state = profile.state,
                    fieldErrors = emptyMap()
                )
            }
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
                _state.value = PatientProfileState.Error(UiText.DynamicString("Please fix form errors"))
                return@launch
            }

            _state.value = PatientProfileState.Loading
            when(val result = updatePatientProfileUseCase(
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
            )) {
                is Result.Success -> {
                    preferences.saveString(PreferenceKeys.PATIENT_NAME, form.fullName)
                    _isEditing.value = false // Exit edit mode on success

                    val updatedProfile = ApiPatientProfile(
                        name = form.fullName,
                        guardianName = form.guardianName,
                        age = form.dob,
                        address = form.address,
                        email = form.email,
                        city = form.city,
                        state = form.state,
                        bloodGroup = form.bloodGroup,
                        gender = form.gender.label
                    )
                    _formState.value = form.copy(fieldErrors = emptyMap())
                    _state.value = PatientProfileState.UpdateSuccess(updatedProfile)
                }
                is Result.Error -> {
                    _state.value = PatientProfileState.Error(mapErrorToUiText(result.error))
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
    ): Map<String, String>? {
        val errors = mutableMapOf<String, String>()
        if (fullName.isBlank()) errors["fullName"] = "Please enter a valid name"
        if (guardianName.isBlank()) errors["guardianName"] = "Please enter a valid guardian name"
        if (dob.isBlank()) errors["dob"] = "Please enter a valid date of birth"
        if (bloodGroup.isBlank()) errors["bloodGroup"] = "Please select a blood group"
        if (email.isBlank() || !isValidEmail(email)) errors["email"] = "Please enter a valid email address"
        if (address.isBlank()) errors["address"] = "Please enter a valid address"
        if (city.isBlank()) errors["city"] = "Please enter a valid city"
        if (state.isBlank()) errors["state"] = "Please enter a valid state"
        return errors.ifEmpty { null }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
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
            is AuthError.ServerMessage -> UiText.DynamicString(error.message)
            is NetworkError.NoInternet -> UiText.StringResource(Res.string.error_no_internet)
            is NetworkError.Timeout -> UiText.StringResource(Res.string.error_timeout)
            is ServerError -> UiText.StringResource(Res.string.error_server)
            else -> UiText.StringResource(Res.string.error_unknown)
        }
    }
}

sealed class PatientProfileState {
    object Idle : PatientProfileState()
    object Loading : PatientProfileState()
    data class FetchSuccess(val profile: ApiPatientProfile) : PatientProfileState()
    data class UpdateSuccess(val profile: ApiPatientProfile) : PatientProfileState()
    data class Error(val message: UiText) : PatientProfileState()
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