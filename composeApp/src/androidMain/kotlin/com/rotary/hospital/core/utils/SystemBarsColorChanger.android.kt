package com.rotary.hospital.core.utils

import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import com.rotary.hospital.core.common.Logger

@Composable
fun findActivity(): ComponentActivity? {
    val context = LocalView.current.context
    var currentContext = context
    while (currentContext is ContextWrapper) {
        if (currentContext is ComponentActivity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
actual fun SetSystemBars(
    statusBarColor: Color?,
    navigationBarColor: Color?,
    iconStyle: BarIcons
) {
    Logger.d(
        "SetSystemBars",
        "SetSystemBars called with statusBarColor: $statusBarColor, navigationBarColor: $navigationBarColor, iconStyle: $iconStyle"
    )
    val activity = findActivity()
    if (activity == null) return
    SideEffect {
        if (statusBarColor == null && navigationBarColor == null) {
            val window = activity.window
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.isAppearanceLightStatusBars = (iconStyle == BarIcons.Dark)
            controller.isAppearanceLightNavigationBars = (iconStyle == BarIcons.Dark)
        } else {
            activity.enableEdgeToEdge(
                statusBarStyle = when (iconStyle) {
                    BarIcons.Dark -> SystemBarStyle.light(
                        scrim = (statusBarColor?.toArgb() ?: Color.Transparent.toArgb()),
                        darkScrim = Color.Black.toArgb()
                    )

                    BarIcons.Light -> SystemBarStyle.dark(
                        scrim = (statusBarColor?.toArgb() ?: Color.Transparent.toArgb())
                    )
                },
                navigationBarStyle = when (iconStyle) {
                    BarIcons.Dark -> SystemBarStyle.light(
                        scrim = (navigationBarColor?.toArgb() ?: Color.Transparent.toArgb()),
                        darkScrim = Color.Black.toArgb()
                    )

                    BarIcons.Light -> SystemBarStyle.dark(
                        scrim = (navigationBarColor?.toArgb() ?: Color.Transparent.toArgb())
                    )
                }
            )
        }
        Logger.d("SetSystemBars", "Colors + icons updated")
    }
}
