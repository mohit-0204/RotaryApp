package com.rotary.hospital

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rotary.hospital.app.App
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.GrayWhite
import com.rotary.hospital.core.theme.White
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

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
            App(paymentHandler = paymentHandler)
        }
    }
}
