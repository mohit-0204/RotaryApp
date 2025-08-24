package com.rotary.hospital.feature.home.presentation.viewmodel

import com.rotary.hospital.feature.home.domain.model.ContactSection

data class ContactUsUiState(
    val loading: Boolean = true,
    val sections: List<ContactSection> = emptyList(),
    val error: String? = null
)
