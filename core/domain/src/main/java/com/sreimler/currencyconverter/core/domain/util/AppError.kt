package com.sreimler.currencyconverter.core.domain.util

sealed class AppError {
    object RequestTimeout : AppError()
    object TooManyRequests : AppError()
    object NoInternet : AppError()
    object ServerError : AppError()
    object InvalidRequest : AppError()
    object RefreshNotAllowed : AppError()
    object Serialization : AppError()
    object Unauthorized : AppError()
    object NotFound : AppError()
    data class Unknown(val cause: Throwable?) : AppError()
}