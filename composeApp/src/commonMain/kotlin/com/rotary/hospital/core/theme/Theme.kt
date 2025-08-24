package com.rotary.hospital.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//  Define your original primary colors
val White = Color(0xFFFFFFFF)
val GreenishGrayBackground = Color(0xFFF2F8F7)

val ColorPrimary = Color(0xFF05968a)
val ColorPrimaryContainer = Color(0xFF1c7a70)
val Surface = Color(0xFFf4fbf8)
val Background = GreenishGrayBackground
val SurfaceContainer = Color(0xFFe3eae7)
val OnSurface = Color(0xFF0c1211)
val OnSurfaceVariant = Color(0xFF2e3836)
val Error = Color(0xFFf16c60)

val ColorPrimaryDark = Color(0xFF97ece0)
val OnColorPrimaryDark = Color(0xFF002b27)
val ColorPrimaryContainerDark = Color(0xFF499e94)
val OnColorPrimaryContainerDark = Color(0xFF000000)
val SurfaceDark = Color(0xFF000000)
val BackgroundDark = Color(0xFF003732)
val SurfaceVariantDark = Color(0xFF232928)
val OnSurfaceDark = Color(0xFFffffff)
val OnSurfaceVariantDark = Color(0xFFffffff)
val ErrorDark = Color(0xFFffd2cc)
val OnErrorDark = Color(0xFF540003)

//  Light theme color scheme
private val LightColors = lightColorScheme(
    primary = ColorPrimary,
    onPrimary = White,
    primaryContainer = ColorPrimaryContainer,
    onPrimaryContainer = White,
    surface = Surface,
    background = Background,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    onError = White,
)

//  Dark theme color scheme
private val DarkColors = darkColorScheme(
    primary = ColorPrimaryDark,
    onPrimary = OnColorPrimaryDark,
    primaryContainer = ColorPrimaryContainerDark,
    onPrimaryContainer = OnColorPrimaryContainerDark,
    surface = SurfaceDark,
    background = BackgroundDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
)

// ✅ Theme wrapper
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val theme = if (isSystemInDarkTheme()) LightColors else LightColors
    MaterialTheme(
        colorScheme = theme,
        typography = Typography(),
        content = content
    )
}
