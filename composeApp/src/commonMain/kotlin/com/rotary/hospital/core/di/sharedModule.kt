package com.rotary.hospital.core.di

import com.rotary.hospital.feature.auth.data.network.AuthService
import com.rotary.hospital.feature.auth.data.repository.AuthRepositoryImpl
import com.rotary.hospital.feature.auth.domain.repository.AuthRepository
import com.rotary.hospital.feature.auth.domain.usecase.SendOtpUseCase
import com.rotary.hospital.feature.auth.domain.usecase.VerifyOtpUseCase
import com.rotary.hospital.feature.auth.presentation.viewmodel.LoginViewModel
import com.rotary.hospital.feature.auth.presentation.viewmodel.OtpViewModel
import com.rotary.hospital.feature.patient.di.patientModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sharedModule = module {

    single { AuthService() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { SendOtpUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    viewModel { LoginViewModel(get(), get()) }
    factory { OtpViewModel(get(), get()) }
    includes(patientModule)
}
