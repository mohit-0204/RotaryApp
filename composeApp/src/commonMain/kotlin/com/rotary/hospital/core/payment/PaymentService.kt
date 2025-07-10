package com.rotary.hospital.core.payment

expect class PaymentService {
    fun initialize(context: Any?)
    fun initiatePayment(
        payloadBase64: String,
        checksum: String,
        apiEndPoint: String,
        onResult: (Boolean) -> Unit,
        context: Any?
    )
}