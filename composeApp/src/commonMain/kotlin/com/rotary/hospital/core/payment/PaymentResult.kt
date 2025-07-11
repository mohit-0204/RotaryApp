package com.rotary.hospital.core.payment

sealed class PaymentResult {
    data class Success(val response: String) : PaymentResult()
    data class Failure(val message: String) : PaymentResult()
    object Cancelled : PaymentResult()
}