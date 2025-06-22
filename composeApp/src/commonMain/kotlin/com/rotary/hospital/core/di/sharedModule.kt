package com.rotary.hospital.core.di

import com.rotary.hospital.feature.auth.di.authModule
import com.rotary.hospital.feature.opd.di.opdModule
import com.rotary.hospital.feature.patient.di.patientModule
import org.koin.dsl.module

val sharedModule = module {


    includes(authModule,patientModule, opdModule)
}
