package com.rotary.hospital.core.payment

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment

actual class PaymentService {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    actual fun initialize(context: Any?) {
        if (context is Context) {
            PhonePe.init(context, PhonePeEnvironment.RELEASE, "M220RUD42CZ5M", null)

            // Ideally, this should be set up in a Composable or Activity
            // For now, we'll assume it's set externally
        }
    }

    fun setActivityResultLauncher(launcher: ActivityResultLauncher<Intent>) {
        this.activityResultLauncher = launcher
    }

    actual fun initiatePayment(
        payloadBase64: String,
        checksum: String,
        apiEndPoint: String,
        onResult: (Boolean) -> Unit,
        context: Any? // Add context parameter
    ) {
        if (context !is Context) return onResult(false)

        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(apiEndPoint)
            .build()

        val intent = PhonePe.getImplicitIntent(
            context,
            b2BPGRequest,
            "com.phonepe.simulator"
        )
        if (intent != null) {
            activityResultLauncher.launch(intent)
            onResult(true)
        } else {
            onResult(false)
        }
    }

}