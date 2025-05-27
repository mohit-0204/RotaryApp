package com.rotary.hospital.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Patient(val id: String, val name: String, val phoneNumber: String)