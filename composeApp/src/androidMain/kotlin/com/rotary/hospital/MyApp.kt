package com.rotary.hospital

import android.app.Application
import android.util.Log
import com.rotary.hospital.core.di.androidModule
import com.rotary.hospital.core.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            Log.d("MyApp", "Application onCreate called, starting Koin")
            androidContext(this@MyApp)
            modules(androidModule+ sharedModule)
        }
    }
}
