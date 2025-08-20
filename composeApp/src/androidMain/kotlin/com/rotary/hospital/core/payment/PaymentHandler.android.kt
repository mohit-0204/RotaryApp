package com.rotary.hospital.core.payment

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.rotary.hospital.core.common.Logger

actual class PaymentHandler(
    private val activity: ComponentActivity,
    private val paymentResultLauncher: ActivityResultLauncher<Intent>
) {
    private var onResultCallback: ((PaymentResult) -> Unit)? = null

    init {
        try {

            PhonePe.init(
                activity.applicationContext,
                PhonePeEnvironment.SANDBOX, // Use RELEASE for production & SANDBOX for testing
//                "M220RUD42CZ5M",
                "PHONEPEPGUAT8",
                null
            )
            Logger.d("PaymentHandler", "PhonePe SDK initialized")
        } catch (e: PhonePeInitException) {
            Logger.e("PaymentHandler", "Init failed: ${e.message}")
        }
    }

    fun logIntentData(data: Intent?) {
        if (data == null) {
            Logger.d("PaymentHandler", "Intent data = null")
            return
        }

        val extras = data.extras
        if (extras == null) {
            Logger.d("PaymentHandler", "Intent has no extras")
            return
        }

        for (key in extras.keySet()) {
            val value = extras.get(key)
            Logger.d("PaymentHandler", "Intent extra → $key = $value")
        }
    }

    fun handleActivityResult(resultCode: Int, data: Intent?) {
        logIntentData(data)
        val result = when (resultCode) {
            Activity.RESULT_OK -> {
                PaymentResult.Success
            }
            Activity.RESULT_CANCELED -> PaymentResult.Cancelled
            else -> PaymentResult.Failure("Unknown result code: $resultCode")
        }

        onResultCallback?.invoke(result)
        onResultCallback = null
    }

    actual fun startPayment(
        base64Body: String,
        checksum: String,
        apiEndPoint: String,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val b2BPGRequest = B2BPGRequestBuilder()
                .setData(base64Body)
                .setChecksum(checksum)
                .setUrl(apiEndPoint)
                .build()

            val intent = PhonePe.getImplicitIntent(
                activity,
                b2BPGRequest,
                "com.phonepe.simulator" // Target PhonePe simulator app (change in real world)
            )

            if (intent != null) {
                onResultCallback = onResult
                paymentResultLauncher.launch(intent)
            } else {
                onResult(PaymentResult.Failure("Intent is null"))
            }
        } catch (e: Exception) {
            onResult(PaymentResult.Failure("Exception: ${e.message ?: "Unknown error"}"))
        }
    }
}
