package com.rotary.hospital.core.data.preferences

import com.rotary.hospital.createDataStore

actual fun createPreferencesManager(): PreferencesManager {
    return PreferencesManagerImpl(createDataStore())
}