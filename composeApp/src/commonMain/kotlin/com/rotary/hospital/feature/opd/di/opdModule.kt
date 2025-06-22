package com.rotary.hospital.feature.opd.di

import org.koin.core.module.dsl.viewModel
import com.rotary.hospital.feature.opd.data.remote.OpdService
import com.rotary.hospital.feature.opd.data.repository.OpdRepositoryImpl
import com.rotary.hospital.feature.opd.domain.repository.OpdRepository
import com.rotary.hospital.feature.opd.domain.usecase.*
import com.rotary.hospital.feature.opd.presentation.viewmodel.*
import org.koin.dsl.module

val opdModule = module {
    single { OpdService() }
    single<OpdRepository> { OpdRepositoryImpl(get()) }
    single { GetBookedOpdsUseCase(get()) }
    single { GetRegisteredPatientsUseCase(get()) }
    single { GetSpecializationsUseCase(get()) }
    single { GetDoctorsUseCase(get()) }
    single { GetSlotsUseCase(get()) }
    single { GetAvailabilityUseCase(get()) }
    single { GetDoctorAvailabilityUseCase(get()) }
    single { GetPaymentReferenceUseCase(get()) }
    single { GetPaymentStatusUseCase(get()) }
    single { InsertOpdUseCase(get()) }
    viewModel{ RegisteredOpdsViewModel(get()) }
    viewModel{ OpdPatientListViewModel(get(), get()) }
    viewModel{ RegisterNewOpdViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel{ DoctorAvailabilityViewModel(get()) }
    viewModel{ OpdPaymentSuccessViewModel(get()) }
    viewModel{ OpdPaymentPendingViewModel() }
    viewModel{ OpdPaymentFailedViewModel() }
    viewModel{ SelectedOpdDetailsViewModel(get()) }
}