package com.rotary.hospital.core.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import okhttp3.Dns
import java.net.InetAddress

// Custom DNS
private class CustomDns : Dns {
    private val hostMap = mapOf(
        "dev.erp.mdi" to "192.51.11.206",
        "dev.erp.hospital" to "192.51.11.206",
        "localerp.mdi" to "192.51.99.216"
    )

    override fun lookup(hostname: String): List<InetAddress> {
        val ip = hostMap[hostname]
        return if (ip != null) {
            listOf(InetAddress.getByName(ip))
        } else {
            Dns.SYSTEM.lookup(hostname)
        }
    }
}

actual fun provideHttpClient(): HttpClient {
    val okHttpClient = okhttp3.OkHttpClient.Builder()
        .dns(CustomDns())
        .build()

    return HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient
        }
    }
}
