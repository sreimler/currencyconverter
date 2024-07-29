package com.sreimler.currencyconverter.core.data

import com.sreimler.currencyconverter.core.domain.ExchangeRate

fun ExchangeRate.toExchangeRateSerializable(): ExchangeRateSerializable {
    return ExchangeRateSerializable(
        currency = currency.toCurrencySerializable(),
        baseCurrency = baseCurrency.toCurrencySerializable(),
        rate = rate
    )
}

fun ExchangeRateSerializable.toExchangeRate(): ExchangeRate {
    return ExchangeRate(
        currency = currency.toCurrency(),
        baseCurrency = baseCurrency.toCurrency(),
        rate = rate
    )
}