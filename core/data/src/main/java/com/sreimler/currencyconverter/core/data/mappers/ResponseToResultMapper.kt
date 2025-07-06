package com.sreimler.currencyconverter.core.data.mappers

import com.sreimler.currencyconverter.core.domain.util.AppError
import com.sreimler.currencyconverter.core.domain.util.AppResult
import kotlinx.serialization.SerializationException
import retrofit2.Response
import java.io.IOException


/**
 * Maps a Retrofit `Response` object to an `AppResult` object.
 * Transforms the response body if the HTTP status code indicates success, or maps errors to appropriate `AppError` types.
 *
 * @param T The type of the response body.
 * @param R The type of the transformed result.
 * @param response The Retrofit response to be mapped.
 * @param transform A function to transform the response body into the desired result type.
 * @return An `AppResult` object representing either a successful transformation or an error.
 */
fun <T, R> mapResponseToResult(
    response: Response<T>,
    transform: (T) -> R
): AppResult<R> {
    return when (response.code()) {
        in 200..299 -> {
            try {
                val body = response.body()
                if (body != null) {
                    AppResult.Success(transform(body))
                } else {
                    AppResult.Error(AppError.Serialization)
                }
            } catch (e: IOException) {
                AppResult.Error(AppError.NoInternet)
            } catch (e: SerializationException) {
                AppResult.Error(AppError.Serialization)
            }
        }

        401, 403 -> AppResult.Error(AppError.Unauthorized)
        408 -> AppResult.Error(AppError.RequestTimeout)
        422 -> AppResult.Error(AppError.InvalidRequest)
        429 -> AppResult.Error(AppError.TooManyRequests)
        in 500..599 -> AppResult.Error(AppError.ServerError)
        else -> AppResult.Error(AppError.Unknown(response.errorBody()?.string().let { Throwable(it) }))
    }
}