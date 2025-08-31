package com.rotary.hospital

import androidx.compose.ui.window.ComposeUIViewController
import com.rotary.hospital.app.App
import com.rotary.hospital.core.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}