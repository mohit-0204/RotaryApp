package com.rotary.hospital.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotary.hospital.OtpAction
import com.rotary.hospital.OtpState
import com.rotary.hospital.PreferencesManager
import com.rotary.hospital.utils.PreferenceKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class OtpViewModel(
    private val preferences: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

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


    fun onAction(action: OtpAction) {
        when (action) {
            is OtpAction.OnChangeFieldFocus -> {
                _state.update {
                    it.copy(
                        focusedIndex = action.index
                    )
                }
            }

            is OtpAction.OnEnterNumber -> {
                enterNumber(action.number,action.index)
            }

            OtpAction.OnKeyBoardBack -> {
                val previousIndex = getPreviousFocusedIndex(state.value.focusedIndex)
                _state.update { it.copy(
                    code =  it.code.mapIndexed { index,number->
                        if(index== previousIndex){
                            null
                        }else{
                            number
                        }
                    },
                    focusedIndex = previousIndex
                ) }
            }

            OtpAction.ClearFields -> {
                _state.update {
                    it.copy(
                        code = List(4) { null },
                        focusedIndex = 0,
                        isValid = null
                    )
                }
            }        }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun enterNumber(number: Int?, index: Int) {
        val newCode = state.value.code.mapIndexed { currentIndex, currentNumber ->
            if (currentIndex == index) {
                number
            } else {
                currentNumber
            }
        }
        val wasNumberRemoved = number == null
        _state.update {
            it.copy(
                code = newCode,
                focusedIndex = if (wasNumberRemoved || it.code.getOrNull(index) != null) {
                    it.focusedIndex
                } else {
                    getNextFocusedTextFieldIndex(
                        currentCode = it.code,
                        currentFocusedIndex = it.focusedIndex
                    )
                },
                isValid = if(newCode.none{it == null}){
                    newCode.joinToString("")==storedOtp.value
                }else null
            )
        }
    }

    private fun getNextFocusedTextFieldIndex(
        currentCode: List<Int?>,
        currentFocusedIndex: Int?
    ): Int? {
        if (currentFocusedIndex == null) {
            return null
        }
        if (currentFocusedIndex == 3) {
            return currentFocusedIndex
        }
        return getFirstEmptyFieldIndexAfterFocusedIndex(
            code = currentCode,
            currentFocusedIndex = currentFocusedIndex
        )
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        code: List<Int?>,
        currentFocusedIndex: Int
    ): Int {
        code.forEachIndexed { index, number ->
            if (index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if (number == null) {
                return index
            }
        }
        return currentFocusedIndex
    }

}