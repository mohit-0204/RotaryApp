package com.rotary.hospital.feature.home.presentation.model

sealed class HomeAction {
    data object ViewPatientProfile : HomeAction()
    data object BookOPD : HomeAction()
    data object ViewLabTests : HomeAction()
    data object ManageMedicineReminders : HomeAction()
    data object ContactUs : HomeAction()
    data object OpenSettings : HomeAction()
    data object ViewTerms : HomeAction()
}