package com.sreimler.currencyconverter.core.presentation.util

import android.content.Context
import com.sreimler.currencyconverter.core.domain.util.AppError
import com.sreimler.currencyconverter.core.presentation.R

fun AppError.toString(context: Context): String {
    val resId = when (this) {
        AppError.InvalidRequest -> R.string.error_invalid_request         // e.g. invalid currency selection
        AppError.NoInternet -> R.string.error_no_internet                 // user offline or connection issues
        AppError.NotFound -> R.string.error_not_found                     // requested currency or rate not found
        AppError.RequestTimeout -> R.string.error_request_timeout         // slow connection
        AppError.Serialization -> R.string.error_serialization            // malformed response from server
        AppError.ServerError -> R.string.error_server                     // 5xx level server issue
        AppError.TooManyRequests -> R.string.error_too_many_requests      // API rate limit
        AppError.Unauthorized -> R.string.error_unauthorized              // missing/invalid API key
        AppError.RefreshNotAllowed -> R.string.refresh_interval_too_short // refresh interval too short
        is AppError.Unknown -> R.string.error_unknown                     // fallback
    }

    return context.getString(resId)
}