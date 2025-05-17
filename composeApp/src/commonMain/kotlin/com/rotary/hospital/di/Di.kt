package com.rotary.hospital.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
//import com.rotary.hospital.RegistrationViewModel
import org.koin.dsl.module

val appModule = module {
//    single { RegistrationViewModel() }
    single<DataStore<Preferences>> { createDataStore() } // Platform-specific DataStore creation
}

// Placeholder for platform-specific DataStore creation (implemented in platform modules)
expect fun createDataStore(): DataStore<Preferences>