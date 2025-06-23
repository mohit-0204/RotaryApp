package com.rotary.hospital.core.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ToastController {
    private var snackbarHostState: SnackbarHostState? = null
    private var coroutineScope: CoroutineScope? = null

    fun bind(hostState: SnackbarHostState, scope: CoroutineScope) {
        snackbarHostState = hostState
        coroutineScope = scope
    }

    fun show(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        coroutineScope?.launch {
            snackbarHostState?.showSnackbar(message, duration = duration)
        }
    }
}

// Singleton instance
val toastController = ToastController()
