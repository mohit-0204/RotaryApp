package com.rotary.hospital.core.utils

import androidx.compose.runtime.Composable
import platform.darwin.NSObject

actual class PlatformContext(val nsObject: NSObject)

@Composable
actual fun rememberPlatformContext(): PlatformContext? {
    return PlatformContext(nsObject = NSObject()) // Provide a default NSObject
}