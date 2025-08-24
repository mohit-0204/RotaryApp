package com.rotary.hospital.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class BarIcons { Light, Dark }

@Composable
expect fun SetSystemBars(
    statusBarColor: Color? = null,
    navigationBarColor: Color? = null,
    iconStyle: BarIcons = BarIcons.Dark
)