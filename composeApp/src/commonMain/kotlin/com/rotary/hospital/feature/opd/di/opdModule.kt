package com.rotary.hospital.feature.opd.di

import com.rotary.hospital.feature.opd.data.network.OpdService
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
    factory { RegisteredOpdsViewModel(get()) }
    factory { OpdPatientListViewModel(get(), get()) }
    factory { RegisterNewOpdViewModel(get(), get(), get(), get(), get(), get()) }
    factory { DoctorAvailabilityViewModel(get()) }
    factory { OpdPaymentSuccessViewModel(get()) }
    factory { OpdPaymentPendingViewModel() }
    factory { OpdPaymentFailedViewModel() }
    factory { SelectedOpdDetailsViewModel(get()) }
}