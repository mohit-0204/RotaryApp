package com.rotary.hospital.core.data.preferences

import org.koin.java.KoinJavaComponent.get

actual fun createPreferencesManager(): PreferencesManager {
    return get(PreferencesManager::class.java)
}