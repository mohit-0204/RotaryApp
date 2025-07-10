package com.rotary.hospital.core.payment

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.rotary.hospital.core.common.Logger

actual class PaymentHandler(
    private val activity: ComponentActivity,
    private val paymentResultLauncher: ActivityResultLauncher<Intent>
) {
    private var onResultCallback: ((String) -> Unit)? = null

    init {
        Logger.d("PaymentHandler", "Initialized")
        try {
            PhonePe.init(
                activity.applicationContext,
                PhonePeEnvironment.SANDBOX, // Use RELEASE for production & SANDBOX for testing
//                "M220RUD42CZ5M",
                "PGTESTPAYUAT86",
                null
            )
        } catch (e: PhonePeInitException) {
            e.printStackTrace()
        }
    }
    fun handleActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val response = data?.getStringExtra("response") ?: "No response"
            onResultCallback?.invoke("Payment Success: $response")
        } else {
            onResultCallback?.invoke("Payment Failed or Cancelled")
        }
        onResultCallback = null
    }
    actual fun startPayment(
        base64Body: String,
        checksum: String,
        apiEndPoint: String,
        onResult: (String) -> Unit
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
                "com.phonepe.simulator" // Target PhonePe simulator app
//                "com.phonepe.simulator" // Target PhonePe app
            )
            onResultCallback = onResult
            if (intent != null)
                paymentResultLauncher.launch(intent)
            else
                onResult("Error Occurred")
        } catch (e: PhonePeInitException) {
            onResult("Error initiating payment: ${e.message}")
        }
    }
}