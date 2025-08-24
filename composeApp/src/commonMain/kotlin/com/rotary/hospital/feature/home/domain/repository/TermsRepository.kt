package com.rotary.hospital.feature.home.domain.repository

interface TermsRepository {
    suspend fun getTermsHtml(): String
}
