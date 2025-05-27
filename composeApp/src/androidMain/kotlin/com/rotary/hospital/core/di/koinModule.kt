package com.rotary.hospital.core.di

import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.data.preferences.PreferencesManagerImpl
import com.rotary.hospital.createDataStore
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val androidModule = module {
    single<PreferencesManager> {
        PreferencesManagerImpl(createDataStore(androidContext()))
    }
}
