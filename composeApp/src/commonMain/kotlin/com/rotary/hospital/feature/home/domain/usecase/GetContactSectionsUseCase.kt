package com.rotary.hospital.feature.home.domain.usecase

import com.rotary.hospital.feature.home.domain.model.ContactSection
import com.rotary.hospital.feature.home.domain.repository.ContactRepository

class GetContactSectionsUseCase(
    private val repo: ContactRepository
) {
    suspend operator fun invoke(): Result<List<ContactSection>> = repo.getSections()
}
