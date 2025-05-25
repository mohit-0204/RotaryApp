package com.rotary.hospital.feature.auth.domain.repository

import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse

interface AuthRepository {
    suspend fun sendOtp(mobileNumber: String): SmsVerificationResponse
    suspend fun verifyOtp(mobileNumber: String, otpCode: String): SmsVerificationResponse
}