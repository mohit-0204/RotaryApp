package com.rotary.hospital.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.domain.AppError
import com.rotary.hospital.core.domain.AuthError
import com.rotary.hospital.core.domain.NetworkError
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.core.domain.ServerError
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.domain.UiText
import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import com.rotary.hospital.feature.auth.domain.usecase.SendOtpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rotaryhospital.composeapp.generated.resources.*

class LoginViewModel(
    private val sendOtpUseCase: SendOtpUseCase,
    private val preferences: PreferencesManager
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _mobileNumber = MutableStateFlow("")
    val mobileNumber: StateFlow<String> = _mobileNumber.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.getString(PreferenceKeys.MOBILE_NUMBER, "").collect { savedMobile ->
                if (savedMobile.isNotBlank()) {
                    _mobileNumber.value = savedMobile
                }
            }
        }
    }

    fun setMobileNumber(number: String) {
        _mobileNumber.value = number
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun sendOtp() {
        if (_mobileNumber.value.length != 10) {
            _loginState.value = LoginState.Error(
                UiText.StringResource(Res.string.error_invalid_mobile_number)
            )
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            when (val result = sendOtpUseCase(_mobileNumber.value)) {
                is Result.Success -> {
                    preferences.saveString(PreferenceKeys.MOBILE_NUMBER, _mobileNumber.value)
                    _loginState.value = LoginState.Success(result.data)
                }
                is Result.Error -> {
                    _loginState.value = LoginState.Error(mapErrorToUiText(result.error))
                }
            }
        }
    }

    private fun mapErrorToUiText(error: AppError): UiText {
        return when (error) {
            is AuthError.ServerMessage -> UiText.DynamicString(error.message)
            is AuthError.SmsSendFailed -> UiText.StringResource(Res.string.error_send_sms_failed)
            is AuthError.InvalidOtp -> UiText.StringResource(Res.string.error_invalid_otp) // Should not happen here but good to be exhaustive

            is NetworkError.NoInternet -> UiText.StringResource(Res.string.error_no_internet)
            is NetworkError.Timeout -> UiText.StringResource(Res.string.error_timeout)

            is ServerError -> UiText.StringResource(Res.string.error_server)
            else -> UiText.StringResource(Res.string.error_unknown)
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: SmsVerificationResponse) : LoginState()
    data class Error(val message: UiText) : LoginState()
}