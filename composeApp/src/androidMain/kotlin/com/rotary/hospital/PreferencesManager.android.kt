package com.rotary.hospital

import android.content.Context
import org.koin.java.KoinJavaComponent.get

actual fun createPreferencesManager(): PreferencesManager {
    return get(PreferencesManager::class.java)
}