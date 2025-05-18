package com.rotary.hospital

actual fun createPreferencesManager(): PreferencesManager {
    return PreferencesManagerImpl(createDataStore())
}