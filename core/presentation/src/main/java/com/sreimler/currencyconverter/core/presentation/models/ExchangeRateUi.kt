package com.sreimler.currencyconverter.core.presentation.models

import com.sreimler.currencyconverter.core.domain.ExchangeRate

data class ExchangeRateUi(
    val targetCurrency: CurrencyUi,
    val baseCurrency: CurrencyUi,
    val rate: Double,
    val dateTimeUtc: String
)

fun ExchangeRate.toExchangeRateUi(): ExchangeRateUi {
    return ExchangeRateUi(
        targetCurrency = currency.toCurrencyUi(),
        baseCurrency = rateBaseCurrency.toCurrencyUi(),
        rate = rate,
        dateTimeUtc = dateTimeUtc.toString()
    )
}