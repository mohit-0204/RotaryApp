package com.rotary.hospital.feature.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SmsVerificationResponse(
    val response: Boolean,
    val message: String,
    val verification: Boolean? = null,
    val patients: Int? = null
)