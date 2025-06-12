package com.sreimler.currencyconverter.startup

import android.icu.util.Currency
import com.sreimler.currencyconverter.core.data.config.SupportedCurrencies
import com.sreimler.currencyconverter.core.data.config.SupportedCurrencies.DEFAULT_BASE_CURRENCY
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import timber.log.Timber
import java.util.Locale

suspend fun initializePreferredCurrency(currencyStorage: LocalPreferredCurrencyStorage) {
    val supportedCurrencies = SupportedCurrencies.codes

    Timber.i("Trying to retrieve default currency..")
    val locale = Locale.getDefault()
    val currency = try {
        Currency.getInstance(locale)
    } catch (e: IllegalArgumentException) {
        Timber.w(e, "Not country or no corresponding currency found")
        null
    }

    val currencyCode = currency?.currencyCode
    val baseCurrency = if (currencyCode != null && currencyCode in supportedCurrencies) {
        Timber.i("Found local currency $currencyCode")
        currencyCode
    } else {
        Timber.i("No local currency found - defaulting to $DEFAULT_BASE_CURRENCY")
        DEFAULT_BASE_CURRENCY
    }

    currencyStorage.initialize(baseCurrency)
}