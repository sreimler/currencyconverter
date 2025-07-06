package com.sreimler.currencyconverter.core.domain.util


typealias EmptyAppResult = AppResult<Unit>


/**
 * A sealed class representing the result of an operation, which can be one of the following:
 * - `Loading`: Indicates that the operation is in progress.
 * - `Success`: Indicates that the operation completed successfully, with optional data and a flag for refreshing state.
 * - `Error`: Indicates that the operation failed, with an associated error and optional data.
 *
 * @param T The type of the data associated with the result.
 */
sealed class AppResult<out T> {

    /**
     * Represents a loading state, typically used to show progress indicators in the UI.
     */
    class Loading<out T> : AppResult<T>()

    /**
     * Represents a successful result of an operation.
     *
     * @param T The type of the data associated with the result.
     * @property data The data returned by the operation.
     * @property isRefreshing A flag indicating whether the data is being refreshed.
     */
    data class Success<T>(val data: T, val isRefreshing: Boolean = false) : AppResult<T>()

    /**
     * Represents an error result of an operation.
     *
     * @param T The type of the data associated with the result.
     * @property error The error that occurred during the operation.
     * @property data Optional data that may have been partially retrieved before the error occurred.
     */
    data class Error<out T>(val error: AppError, val data: T? = null) : AppResult<T>()
}
