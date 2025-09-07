package com.rotary.hospital.core.utils

import com.rotary.hospital.core.domain.AppError
import com.rotary.hospital.core.domain.NetworkError
import com.rotary.hospital.core.domain.UnknownError

// androidMain
actual fun mapPlatformException(t: Throwable): AppError = when (t) {
    is java.net.ConnectException -> NetworkError.NoInternet
    is java.net.SocketTimeoutException -> NetworkError.Timeout
    is java.io.IOException -> NetworkError.NoInternet
    else -> UnknownError(t.message)
}
