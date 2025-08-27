package com.rotary.hospital.core.payment


expect class PaymentHandler {
    fun startPayment(
        base64Body: String,
        checksum: String,
        apiEndPoint: String,
        onResult: (PaymentResult) -> Unit
    )
}