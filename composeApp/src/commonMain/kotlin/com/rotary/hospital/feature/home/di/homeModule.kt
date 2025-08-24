package com.rotary.hospital.feature.home.di

import com.rotary.hospital.feature.home.data.repository.ContactRepositoryImpl
import com.rotary.hospital.feature.home.data.repository.TermsRepositoryImpl
import com.rotary.hospital.feature.home.domain.repository.ContactRepository
import com.rotary.hospital.feature.home.domain.repository.TermsRepository
import com.rotary.hospital.feature.home.domain.usecase.GetContactSectionsUseCase
import com.rotary.hospital.feature.home.domain.usecase.GetTermsHtmlUseCase
import com.rotary.hospital.feature.home.presentation.viewmodel.ContactUsViewModel
import com.rotary.hospital.feature.home.presentation.viewmodel.TermsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val homeModule = module {
    single<ContactRepository> { ContactRepositoryImpl() }
    single< TermsRepository> { TermsRepositoryImpl() }
    factory { GetContactSectionsUseCase(get()) }
    factory { GetTermsHtmlUseCase(get()) }
    viewModel{ ContactUsViewModel(get()) }
    viewModel{ TermsViewModel(get()) }
}