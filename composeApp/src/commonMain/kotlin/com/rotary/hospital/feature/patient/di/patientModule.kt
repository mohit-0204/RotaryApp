package com.rotary.hospital.feature.patient.di

   import com.rotary.hospital.feature.patient.data.network.PatientService
   import com.rotary.hospital.feature.patient.data.repository.PatientRepositoryImpl
   import com.rotary.hospital.feature.patient.domain.repository.PatientRepository
   import com.rotary.hospital.feature.patient.domain.usecase.GetRegisteredPatientsUseCase
   import com.rotary.hospital.feature.patient.domain.usecase.RegisterPatientUseCase
   import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientListViewModel
   import com.rotary.hospital.feature.patient.presentation.viewmodel.PatientRegistrationViewModel
   import org.koin.core.module.dsl.viewModel
   import org.koin.dsl.module

   val patientModule = module {
       single { PatientService() }
       single<PatientRepository> { PatientRepositoryImpl(get()) }
       single { RegisterPatientUseCase(get()) }
       single { GetRegisteredPatientsUseCase(get()) }
       viewModel { PatientListViewModel(get(),get()) }
       viewModel { PatientRegistrationViewModel(get(), get()) }
   }