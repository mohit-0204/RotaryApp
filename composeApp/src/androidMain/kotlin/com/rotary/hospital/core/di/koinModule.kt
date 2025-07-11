package com.rotary.hospital.core.di

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.rotary.hospital.core.data.preferences.PreferencesManager
import com.rotary.hospital.core.data.preferences.PreferencesManagerImpl
import com.rotary.hospital.core.payment.PaymentHandler
import com.rotary.hospital.createDataStore
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val androidModule = module {
    single<PreferencesManager> {
        PreferencesManagerImpl(createDataStore(androidContext()))
    }

    // Add a factory for PaymentHandler (inject only when needed)
    factory { (activity: ComponentActivity, launcher: ActivityResultLauncher<Intent>) ->
        PaymentHandler(activity, launcher)
    }
}
