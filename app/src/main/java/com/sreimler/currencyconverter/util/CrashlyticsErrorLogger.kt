package com.sreimler.currencyconverter.util

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sreimler.currencyconverter.core.domain.util.AppError
import com.sreimler.currencyconverter.core.domain.util.ErrorLogger
import timber.log.Timber


class CrashlyticsErrorLogger : ErrorLogger {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(error: AppError) {
        Timber.w("An AppError occurred: $error")
        crashlytics.log("AppError: $error")
        if (error is AppError.Unknown && error.cause != null) {
            crashlytics.recordException(error.cause as Throwable)
        }
    }
}