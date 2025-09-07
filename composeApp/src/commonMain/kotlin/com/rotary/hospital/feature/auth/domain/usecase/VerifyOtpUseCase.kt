package com.rotary.hospital.feature.auth.domain.usecase

import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import com.rotary.hospital.feature.auth.domain.repository.AuthRepository
import com.rotary.hospital.core.domain.Result

class VerifyOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        mobileNumber: String,
        otpCode: String
    ): Result<SmsVerificationResponse> {
        return authRepository.verifyOtp(mobileNumber, otpCode)
    }
}