package com.rotary.hospital.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import com.rotary.hospital.feature.auth.domain.usecase.VerifyOtpUseCase
import com.rotary.hospital.feature.auth.presentation.screen.OtpAction
import com.rotary.hospital.feature.auth.presentation.screen.OtpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OtpViewModel(
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(OtpState())
    val state: StateFlow<OtpState> = _state.asStateFlow()

    private val _otpState = MutableStateFlow<OtpVerificationState>(OtpVerificationState.Idle)
    val otpState: StateFlow<OtpVerificationState> = _otpState.asStateFlow()

    private var mobileNumber: String = ""

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
                val previousIndex = getPreviousFocusedIndex(state.value.focusedIndex)
                _state.update {
                    it.copy(
                        code = it.code.mapIndexed { index, number ->
                            if (index == previousIndex) null else number
                        },
                        focusedIndex = previousIndex
                    )
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

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun enterNumber(number: Int?, index: Int) {
        val newCode = state.value.code.mapIndexed { currentIndex, currentNumber ->
            if (currentIndex == index) number else currentNumber
        }
        val wasNumberRemoved = number == null
        _state.update {
            it.copy(
                code = newCode,
                focusedIndex = if (wasNumberRemoved || it.code.getOrNull(index) != null) {
                    it.focusedIndex
                } else {
                    getNextFocusedTextFieldIndex(it.code, it.focusedIndex)
                },
                isValid = if (newCode.none { it == null }) {
                    newCode.joinToString("") == "1111"
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