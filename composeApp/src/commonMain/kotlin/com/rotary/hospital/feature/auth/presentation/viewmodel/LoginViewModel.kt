package com.rotary.hospital.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import com.rotary.hospital.feature.auth.domain.usecase.SendOtpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            _loginState.value = LoginState.Error("Please enter a valid 10-digit mobile number")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = sendOtpUseCase(_mobileNumber.value)
            _loginState.value = when {
                result.isSuccess -> {
                    preferences.saveString(PreferenceKeys.MOBILE_NUMBER, _mobileNumber.value)
                    LoginState.Success(result.getOrNull()!!)
                }
                else -> LoginState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: SmsVerificationResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}