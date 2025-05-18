package com.rotary.hospital.di

import com.rotary.hospital.PreferencesManager
import com.rotary.hospital.PreferencesManagerImpl
import com.rotary.hospital.createDataStore
import org.koin.dsl.module

val iosModule = module {
    single<PreferencesManager> { PreferencesManagerImpl(createDataStore()) } // iOS-specific implementation
}
