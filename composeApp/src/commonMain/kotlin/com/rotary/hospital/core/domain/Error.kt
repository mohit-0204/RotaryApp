package com.rotary.hospital.core.domain

/**
 * A base marker interface for all recognized errors in the application.
 */
sealed interface AppError

/**
 * Feature-specific errors for Authentication. In a larger project, this would live
 * in the `feature-auth` module's domain layer.
 */
sealed class AuthError : AppError {
    data object InvalidOtp : AuthError()
    data object SmsSendFailed : AuthError()
    // Use this for any specific message returned by the server's business logic
    data class ServerMessage(val message: String) : AuthError()
}

/**
 * Common, cross-cutting errors related to network connectivity.
 */
sealed class NetworkError : AppError {
    data object Timeout : NetworkError()
    data object NoInternet : NetworkError()
}

/**
 * Common, cross-cutting errors from the server or data parsing.
 */
data class ServerError(val code: Int) : AppError
data class SerializationError(val message: String?) : AppError
data class UnknownError(val message: String?) : AppError