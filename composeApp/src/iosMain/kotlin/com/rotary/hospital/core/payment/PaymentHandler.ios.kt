package com.rotary.hospital.core.payment

import com.rotary.hospital.feature.opd.domain.model.PaymentRequest
import com.rotary.hospital.feature.opd.domain.model.PaymentStatus

actual class PaymentHandler {
    actual fun startPayment(
        base64Body: String,
        checksum: String,
        apiEndPoint: String,
        onResult: (String) -> Unit
    ) {
    }

}