package com.rotary.hospital.di

import com.rotary.hospital.viewmodels.OtpViewModel
import org.koin.dsl.module

val sharedModule = module {
    factory { OtpViewModel(get()) }
}
