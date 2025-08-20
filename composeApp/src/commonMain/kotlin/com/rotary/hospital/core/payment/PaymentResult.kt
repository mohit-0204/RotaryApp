package com.rotary.hospital.core.payment

sealed class PaymentResult {
    object Success : PaymentResult()
    data class Failure(val message: String) : PaymentResult()
    object Cancelled : PaymentResult()
}