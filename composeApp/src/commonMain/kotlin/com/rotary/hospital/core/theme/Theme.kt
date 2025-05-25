package com.rotary.hospital.core.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//  Define your original primary colors
val ColorPrimary = Color(0xFF00A699)
val ColorPrimaryDark = Color(0xFF05968A)
val White = Color(0xFFFFFFFF)
val GrayWhite = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val ErrorRed = Color(0xFFf16c60)

//  Light theme color scheme
private val LightColors = lightColorScheme(
    primary = ColorPrimary,
    onPrimary = White,
    secondary = ColorPrimaryDark,
    background = White,
    onBackground = ColorPrimary,
)

// ✅ Theme wrapper
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}
