package com.rotary.hospital.core.domain

/**
 * A generic sealed class that holds a value on success or an [AppError] on failure.
 * This is used as the return type for all data layer operations.
 */
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val error: AppError) : Result<T>()
}