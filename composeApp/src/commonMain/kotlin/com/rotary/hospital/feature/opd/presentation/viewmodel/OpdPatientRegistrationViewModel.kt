package com.rotary.hospital.feature.opd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.core.domain.UiText
import com.rotary.hospital.feature.patient.data.model.ApiPatient
import com.rotary.hospital.feature.patient.domain.usecase.RegisterPatientUseCase
import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientRegistrationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.error_unknown
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
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

sealed class OpdPatientRegistrationState {
    object Idle : OpdPatientRegistrationState()
    object Loading : OpdPatientRegistrationState()
    data class Success(val patient: ApiPatient) : OpdPatientRegistrationState()
    data class Error(val message: String) : OpdPatientRegistrationState()
}

data class OpdRegistrationFormState(
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

class OpdPatientRegistrationViewModel(
    private val registerPatientUseCase: RegisterPatientUseCase,
    private val preferences: PreferencesManager
) : ViewModel() {
    private val _state =
        MutableStateFlow<OpdPatientRegistrationState>(OpdPatientRegistrationState.Idle)
    val state: StateFlow<OpdPatientRegistrationState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(OpdRegistrationFormState())
    val formState: StateFlow<OpdRegistrationFormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(
                mobileNumber = preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").first()
            )
        }
    }

    fun updateFormState(formState: OpdRegistrationFormState) {
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
            _state.value = OpdPatientRegistrationState.Loading
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
                        // For OPD, do not set logged-in preferences; just return success
                        _state.value = OpdPatientRegistrationState.Success(patient)
                    } else {
                        _state.value = OpdPatientRegistrationState.Error("No patient data received")

                    }
                }

                is Result.Error -> {
                    _state.value = OpdPatientRegistrationState.Error(result.error.toString())
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
        if (fullName.isEmpty()) errors["fullName"] = "Please enter a valid name"
        if (guardianName.isEmpty()) errors["guardianName"] = "Please enter a valid guardian name"
        if (dob.isEmpty()) errors["dob"] = "Please enter a valid date of birth"
        if (bloodGroup.isEmpty()) errors["bloodGroup"] = "Please select a blood group"
        if (email.isEmpty() || !isValidEmail(email)) errors["email"] =
            "Please enter a valid email address"
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