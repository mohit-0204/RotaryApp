package com.rotary.hospital.feature.home.data.repository

import com.rotary.hospital.feature.home.domain.model.Contact
import com.rotary.hospital.feature.home.domain.model.ContactSection
import com.rotary.hospital.feature.home.domain.repository.ContactRepository

class ContactRepositoryImpl : ContactRepository {
    override suspend fun getSections(): Result<List<ContactSection>> =
        Result.success(
            listOf(
                ContactSection(
                    title = "Contact",
                    items = listOf(
                        Contact(label = "Emergency Help Desk 1", phone = "+91 12345 67890"),
                        Contact(label = "Emergency Help Desk 2", phone = "+91 98765 43210"),
                    )
                ),
                ContactSection(
                    title = "Heart Emergency",
                    items = listOf(
                        Contact(label = "Cardiac Helpline", phone = "+91 11223 33445"),
                    )
                ),
                ContactSection(
                    title = "Ambulance",
                    items = listOf(
                        Contact(label = "Ambulance Desk", phone = "+91 102"),
                    )
                ),
                ContactSection(
                    title = "App Issues",
                    items = listOf(
                        Contact(label = "Tech Support 1", phone = "+91 90000 11111"),
                        Contact(label = "Tech Support 2", phone = "+91 90000 22222"),
                    )
                ),
                ContactSection(
                    title = "Address",
                    items = listOf(
                        Contact(
                            label = "Rotary Hospital",
                            phone = null,
                            description = "123, Civil Lines, City – 000000"
                        )
                    )
                )
            )
        )
}