package com.rotary.hospital.feature.home.domain.repository

import com.rotary.hospital.feature.home.domain.model.ContactSection


interface ContactRepository {
    suspend fun getSections(): Result<List<ContactSection>>
}
