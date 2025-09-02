package com.rotary.hospital.app


import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotary.hospital.core.theme.ColorPrimary
import com.rotary.hospital.core.theme.White
import com.rotary.hospital.core.utils.BarIcons
import com.rotary.hospital.core.utils.SetSystemBars
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rotaryhospital.composeapp.generated.resources.Res
import rotaryhospital.composeapp.generated.resources.location_name
import rotaryhospital.composeapp.generated.resources.logo
import rotaryhospital.composeapp.generated.resources.rotary_hospital

@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {

    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000) // keep total splash duration short
        onFinished()
    }

    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 500.dp,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
        label = "Logo Slide"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "Text Fade"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                White
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ColorPrimary),
                modifier = Modifier
                    .offset(y = offsetY)
                    .size(240.dp)
            )

            Spacer(Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(alpha)
            ) {
                Text(
                    text = stringResource(Res.string.rotary_hospital),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorPrimary
                )
                Text(
                    text = stringResource(Res.string.location_name),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorPrimary
                )
            }
        }
    }
}


