package com.rotary.hospital.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.core.common.PreferenceKeys
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import com.rotary.hospital.feature.auth.domain.usecase.SendOtpUseCase
import com.rotary.hospital.feature.auth.domain.usecase.VerifyOtpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OtpViewModel(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val preferences: PreferencesManager
    ) : ViewModel() {
    private val _state = MutableStateFlow(OtpState())
    val state: StateFlow<OtpState> = _state.asStateFlow()

    private val _otpState = MutableStateFlow<OtpVerificationState>(OtpVerificationState.Idle)
    val otpState: StateFlow<OtpVerificationState> = _otpState.asStateFlow()

    private var mobileNumber: String = ""

    private val _storedOtp = MutableStateFlow("")
    val storedOtp = _storedOtp.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.getString(PreferenceKeys.OTP, "")
                    .collect { otp ->
                _storedOtp.value = otp
            }
        }
    }

    fun setMobileNumber(number: String) {
        mobileNumber = number
    }

    fun onAction(action: OtpAction) {
        when (action) {
            is OtpAction.OnChangeFieldFocus -> {
                _state.update { it.copy(focusedIndex = action.index) }
            }
            is OtpAction.OnEnterNumber -> {
                enterNumber(action.number, action.index)
            }
            is OtpAction.OnKeyBoardBack -> {
                val currentFocus = state.value.focusedIndex ?: return
                val code = state.value.code.toMutableList()

                if (code[currentFocus] != null) {
                    // Case 1: current box has value → clear it
                    code[currentFocus] = null
                    _state.update {
                        it.copy(code = code, focusedIndex = currentFocus)
                    }
                } else {
                    // Case 2: go to previous non-null digit and clear
                    val previous = getPreviousFocusedIndex(currentFocus)
                    if (previous != null) {
                        code[previous] = null
                        _state.update {
                            it.copy(code = code, focusedIndex = previous)
                        }
                    }
                }
            }

            is OtpAction.ClearFields -> {
                _state.update {
                    it.copy(
                        code = List(4) { null },
                        focusedIndex = 0,
                        isValid = null
                    )
                }
            }
        }
    }

    fun verifyOtp() {
        val otp = state.value.code.joinToString("") { it?.toString() ?: "" }
        if (otp.length != 4) {
            _otpState.value = OtpVerificationState.Error("Please enter a valid 4-digit OTP")
            return
        }
        viewModelScope.launch {
            _otpState.value = OtpVerificationState.Loading
            val result = verifyOtpUseCase(mobileNumber, otp)
            _otpState.value = when {
                result.isSuccess -> OtpVerificationState.Success(result.getOrNull()!!)
                else -> OtpVerificationState.Error(result.exceptionOrNull()?.message ?: "Invalid OTP")
            }
        }
    }


    fun resendOtp() {
        viewModelScope.launch {
            _otpState.value = OtpVerificationState.Loading
            val result = sendOtpUseCase(mobileNumber)
            _otpState.value = if (result.isSuccess) {
                OtpVerificationState.Idle // OTP resent successfully
            } else {
                OtpVerificationState.Error(result.exceptionOrNull()?.message ?: "Failed to resend OTP")
            }
        }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun enterNumber(number: Int?, index: Int) {
        val oldCode = state.value.code
        val newCode = oldCode.toMutableList()
        newCode[index] = number

        val nextFocus = if (number == null) {
            index // backspacing - stay here
        } else {
            // ✅ if tapped box was filled already → go to next box directly
            if (oldCode[index] != null) {
                (index + 1).coerceAtMost(3)
            } else {
                // ✅ else use your smart "find next empty box" logic
                getNextFocusedTextFieldIndex(newCode, index)
            }
        }

        _state.update { it ->
            it.copy(
                code = newCode,
                focusedIndex = nextFocus,
                isValid = if (newCode.none { it == null }) {
                    newCode.joinToString("") == storedOtp.value
                } else null
            )
        }
    }

    private fun getNextFocusedTextFieldIndex(currentCode: List<Int?>, currentFocusedIndex: Int?): Int? {
        if (currentFocusedIndex == null) return null
        if (currentFocusedIndex == 3) return currentFocusedIndex
        return getFirstEmptyFieldIndexAfterFocusedIndex(currentCode, currentFocusedIndex)
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(code: List<Int?>, currentFocusedIndex: Int): Int {
        code.forEachIndexed { index, number ->
            if (index <= currentFocusedIndex) return@forEachIndexed
            if (number == null) return index
        }
        return currentFocusedIndex
    }
}

sealed class OtpVerificationState {
    object Idle : OtpVerificationState()
    object Loading : OtpVerificationState()
    data class Success(val response: SmsVerificationResponse) : OtpVerificationState()
    data class Error(val message: String) : OtpVerificationState()
}

data class OtpState(
    val code: List<Int?> = (1..4).map { null },
    val focusedIndex: Int? = 0, // Initialize focus on first field
    val isValid: Boolean? = null
)

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int) : OtpAction
    data class OnChangeFieldFocus(val index: Int) : OtpAction
    data object OnKeyBoardBack : OtpAction
    data object ClearFields : OtpAction
}