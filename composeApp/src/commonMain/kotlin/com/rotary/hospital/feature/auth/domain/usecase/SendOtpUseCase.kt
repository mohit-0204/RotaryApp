package com.rotary.hospital.feature.auth.domain.usecase

import com.rotary.hospital.feature.auth.data.model.SmsVerificationResponse
import com.rotary.hospital.feature.auth.domain.repository.AuthRepository
import kotlin.Result

class SendOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<SmsVerificationResponse> {
        return try {
            val response = authRepository.sendOtp(mobileNumber)
            if (response.response) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}