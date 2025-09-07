package com.rotary.hospital.core.utils

import com.rotary.hospital.core.domain.AppError
import com.rotary.hospital.core.domain.NetworkError
import com.rotary.hospital.core.domain.UnknownError
import io.ktor.utils.io.errors.PosixException
import platform.Foundation.NSError
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut

// iosMain
actual fun mapPlatformException(t: Throwable): AppError = when (t) {
    is PosixException -> NetworkError.NoInternet
    is NSError -> when {
        t.domain == NSURLErrorDomain && t.code == NSURLErrorNotConnectedToInternet ->
            NetworkError.NoInternet

        t.domain == NSURLErrorDomain && t.code == NSURLErrorTimedOut ->
            NetworkError.Timeout

        else -> UnknownError("${t.domain} (${t.code}) ${t.localizedDescription}")
    }

    else -> UnknownError(t.message)
}
