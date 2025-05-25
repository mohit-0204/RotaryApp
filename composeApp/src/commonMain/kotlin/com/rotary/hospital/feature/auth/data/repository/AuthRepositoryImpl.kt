package com.rotary.hospital.feature.auth.data.repository

import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import com.rotary.hospital.feature.auth.data.network.AuthService
import com.rotary.hospital.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(private val authService: AuthService) : AuthRepository {
    override suspend fun sendOtp(mobileNumber: String): SmsVerificationResponse {
        return authService.sendOtp(mobileNumber)
    }

    override suspend fun verifyOtp(mobileNumber: String, otpCode: String): SmsVerificationResponse {
        return authService.verifyOtp(mobileNumber, otpCode)
    }
}