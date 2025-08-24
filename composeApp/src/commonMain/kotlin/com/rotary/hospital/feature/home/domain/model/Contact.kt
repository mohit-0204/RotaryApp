package com.rotary.hospital.feature.home.domain.model

data class Contact(
    val label: String,      // e.g., "Emergency Help Desk 1"
    val phone: String?,     // null for non-call items (e.g., address)
    val description: String? = null
)
