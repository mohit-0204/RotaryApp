package com.rotary.hospital.core.utils

import com.rotary.hospital.core.common.Logger
import com.rotary.hospital.core.domain.AppError
import com.rotary.hospital.core.domain.NetworkError
import com.rotary.hospital.core.domain.Result
import com.rotary.hospital.core.domain.SerializationError
import com.rotary.hospital.core.domain.ServerError
import com.rotary.hospital.core.domain.UnknownError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException


suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.Success(apiCall())
    } catch (e: Throwable) {
        Logger.e("safeApiCall", "Exception caught: ${e.message.orEmpty()}", e)
        Result.Error(mapThrowableToNetworkAppError(e))
    }
}

private fun mapThrowableToNetworkAppError(t: Throwable): AppError = when (t) {
    // server didn’t respond within the configured timeout(API is too slow, long-running query, backend lag)
    is HttpRequestTimeoutException -> NetworkError.Timeout
    // The client cannot establish a TCP connection to the host within the configured timeout(No internet, DNS failure, server unreachable)
    is ConnectTimeoutException -> NetworkError.Timeout
    // server responds with a 4xx status code (e.g. 400 Bad Request, 401 Unauthorized, 404 Not Found)
    is ClientRequestException -> UnknownError(t.message) // 4xx
    // The server responds with a 5xx status code (e.g. 500 Internal Server Error, 502 Bad Gateway)
    is ServerResponseException -> ServerError(t.response.status.value)
    // The HTTP response body cannot be parsed into your expected Kotlin model
    is SerializationException -> SerializationError(t.message)
    // Unexpected exception
    else -> mapPlatformException(t)
}

// commonMain
expect fun mapPlatformException(t: Throwable): AppError
