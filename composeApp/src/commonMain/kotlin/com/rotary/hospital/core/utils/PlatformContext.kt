package com.rotary.hospital.core.utils

import androidx.compose.runtime.Composable


expect class PlatformContext

@Composable
expect fun rememberPlatformContext(): PlatformContext?