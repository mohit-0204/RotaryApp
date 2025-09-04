package com.rotary.hospital.feature.home.data.repository

import com.rotary.hospital.feature.home.domain.model.Contact
import com.rotary.hospital.feature.home.domain.model.ContactSection
import com.rotary.hospital.feature.home.domain.repository.ContactRepository

class ContactRepositoryImpl : ContactRepository {
    override suspend fun getSections(): Result<List<ContactSection>> =
        Result.success(
            listOf(
                ContactSection(
                    title = "24x7 EMERGENCY HELP",
                    items = listOf(
                        Contact(label = "Help Desk 1", phone = "+91 90340 56793"),
                        Contact(label = "Help Desk 2", phone = "+91 82228 56794"),
                    )
                ),
                ContactSection(
                    title = "24x7 HEART EMERGENCY",
                    items = listOf(
                        Contact(label = "Heart Help Desk", phone = "+91 98133 34999"),
                    )
                ),
                ContactSection(
                    title = "AMBULANCE",
                    items = listOf(
                        Contact(label = "Ambulance Help Desk", phone = "+91 90340 56797"),
                    )
                ),
                ContactSection(
                    title = "APP ISSUES",
                    items = listOf(
                        Contact(label = "Tech Support 1", phone = "+91 98963 69290"),
                        Contact(label = "Tech Support 2", phone = "+91 92156 75671"),
                    )
                ),
                ContactSection(
                    title = "ADDRESS",
                    items = listOf(
                        Contact(
                            label = "Rotary Hospital Cancer and General Hospital",
                            phone = "0171 2690009",
                            description = "123, Civil Lines, City – 000000"
                        )
                    )
                )
            )
        )
}