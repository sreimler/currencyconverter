package com.sreimler.currencyconverter.core.domain.util


/**
 * Interface for logging application errors.
 * Provides a method to log errors represented by the `AppError` class.
 */
interface ErrorLogger {

    /**
     * Logs the specified application error.
     *
     * @param error The error to be logged, represented as an `AppError` object.
     */
    fun log(error: AppError)
}