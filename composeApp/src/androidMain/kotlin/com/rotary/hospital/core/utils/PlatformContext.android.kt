package com.rotary.hospital.core.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual class PlatformContext(val context: Context)

@Composable
actual fun rememberPlatformContext(): PlatformContext? {
    return PlatformContext(context = LocalContext.current)
}