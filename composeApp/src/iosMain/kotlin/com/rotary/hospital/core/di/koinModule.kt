package com.rotary.hospital.core.di

import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.data.preferences.PreferencesManagerImpl
import com.rotary.hospital.core.data.preferences.createDataStore
import org.koin.dsl.module

val iosModule = module {
    single<PreferencesManager> { PreferencesManagerImpl(createDataStore()) } // iOS-specific implementation
}
