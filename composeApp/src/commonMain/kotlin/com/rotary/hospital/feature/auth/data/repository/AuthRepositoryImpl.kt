package com.rotary.hospital.feature.auth.data.repository

import com.rotary.hospital.core.domain.AuthError
import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import com.rotary.hospital.feature.auth.data.network.AuthService
import com.rotary.hospital.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(private val authService: AuthService) : AuthRepository {
    override suspend fun sendOtp(mobileNumber: String): Result<SmsVerificationResponse> {
        return when (val result = authService.sendOtp(mobileNumber)) {
            is Result.Success -> {
                if (result.data.response) {
                    result
                } else {
                    Result.Error(AuthError.ServerMessage(result.data.message))
                }
            }

            is Result.Error -> result
        }
    }

    override suspend fun verifyOtp(
        mobileNumber: String,
        otpCode: String
    ): Result<SmsVerificationResponse> {
        return when (val result = authService.verifyOtp(mobileNumber, otpCode)) {
            is Result.Success -> {
                if (result.data.response) {
                    if (result.data.verification == true) {
                        result
                    } else {
                        Result.Error(AuthError.InvalidOtp)
                    }
                } else {
                    Result.Error(AuthError.ServerMessage(result.data.message))
                }
            }

            is Result.Error -> result
        }
    }
}