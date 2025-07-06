package com.sreimler.currencyconverter.startup

import android.icu.util.Currency
import com.sreimler.currencyconverter.core.data.config.SupportedCurrencies
import com.sreimler.currencyconverter.core.data.config.SupportedCurrencies.DEFAULT_BASE_CURRENCY
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import timber.log.Timber
import java.util.Locale


/**
 * Initializes the preferred currency for the application based on the device's locale.
 *
 * @param currencyStorage The storage mechanism for saving the preferred currency.
 */
suspend fun initializePreferredCurrency(currencyStorage: LocalPreferredCurrencyStorage) {
    // Retrieve the list of supported currency codes
    val supportedCurrencies = SupportedCurrencies.codes

    Timber.i("Trying to retrieve default currency..")
    // Get the default locale of the device
    val locale = Locale.getDefault()

    // Attempt to retrieve the currency associated with the device's locale
    val currency = try {
        Currency.getInstance(locale)
    } catch (e: IllegalArgumentException) {
        Timber.w(e, "Not country or no corresponding currency found")
        null
    }

    // Extract the currency code from the retrieved currency, if available
    val currencyCode = currency?.currencyCode

    // Determine the base currency to use, defaulting to the application's default base currency if necessary
    val baseCurrency = if (currencyCode != null && currencyCode in supportedCurrencies) {
        Timber.i("Found currency that corresponds to device locale: $currencyCode")
        currencyCode
    } else {
        Timber.i("No device locale currency found - defaulting to $DEFAULT_BASE_CURRENCY")
        DEFAULT_BASE_CURRENCY
    }

    // Initialize the preferred currency in the storage
    currencyStorage.initialize(baseCurrency)
}