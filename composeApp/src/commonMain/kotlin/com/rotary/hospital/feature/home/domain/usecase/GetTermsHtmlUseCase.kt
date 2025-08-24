package com.rotary.hospital.feature.home.domain.usecase

import com.rotary.hospital.feature.home.domain.model.TermsContent
import com.rotary.hospital.feature.home.domain.repository.TermsRepository

class GetTermsHtmlUseCase(private val repo: TermsRepository) {
    suspend operator fun invoke(): TermsContent = TermsContent(repo.getTermsHtml())
}
