package com.rotary.hospital.core.payment

actual class PaymentService {
    actual fun initialize(context: Any?) {
    }

    actual fun initiatePayment(
        payloadBase64: String,
        checksum: String,
        apiEndPoint: String,
        onResult: (Boolean) -> Unit,
        context: Any?
    ) {
    }
}