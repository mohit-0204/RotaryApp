package com.rotary.hospital.core.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// Declare expected provider
expect fun provideHttpClient(): HttpClient

object NetworkClient {
    val httpClient: HttpClient by lazy {
        provideHttpClient().config {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
            expectSuccess = true
        }
    }
}
