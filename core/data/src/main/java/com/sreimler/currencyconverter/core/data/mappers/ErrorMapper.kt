package com.sreimler.currencyconverter.core.data.mappers

import com.sreimler.currencyconverter.core.domain.util.AppError
import kotlinx.serialization.SerializationException
import java.io.IOException


/**
 * Extension function to map a `Throwable` to an `AppError`.
 * Provides a way to convert various types of exceptions into domain-specific error representations.
 *
 * @receiver Throwable The exception to be mapped.
 * @return AppError The corresponding `AppError` based on the type of the exception.
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        // Maps IOExceptions (e.g., network issues) to a NoInternet error
        is IOException -> AppError.NoInternet
        // Maps SerializationException to a Serialization error
        is SerializationException -> AppError.Serialization
        // Maps all other exceptions to an Unknown error, wrapping the original exception
        else -> AppError.Unknown(this)
    }
}