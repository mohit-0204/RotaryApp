package com.rotary.hospital

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rotary.hospital.app.App
import com.rotary.hospital.core.payment.PaymentHandler
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf


val LocalActivity = staticCompositionLocalOf<ComponentActivity?> { null }

class MainActivity : ComponentActivity() {
    private lateinit var paymentResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var paymentHandler: PaymentHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )
        paymentResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            paymentHandler.handleActivityResult(result.resultCode, result.data)
        }
        // Get the handler from Koin with params
        paymentHandler = get { parametersOf(this, paymentResultLauncher) }
        setContent {
            CompositionLocalProvider(LocalActivity provides this) {
                App(paymentHandler = paymentHandler)
            }
        }
    }
}
