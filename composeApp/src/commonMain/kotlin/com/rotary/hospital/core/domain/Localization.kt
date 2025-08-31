package com.rotary.hospital.core.domain

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Localization {
    fun applyLanguage(iso: String)
}


enum class Language(val iso: String){
    English(iso = "en"),
    Hindi(iso = "hi")
}