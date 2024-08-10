package com.sreimler.currencyconverter.core.data.mappers

import com.sreimler.currencyconverter.core.data.networking.ExchangeRateSerializable
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import java.time.ZonedDateTime

fun ExchangeRate.toExchangeRateSerializable(): ExchangeRateSerializable {
    return ExchangeRateSerializable(
        currency = targetCurrency.toCurrencySerializable(),
        baseCurrency = baseCurrency.toCurrencySerializable(),
        rate = rate,
    )
}

fun ExchangeRateSerializable.toExchangeRate(): ExchangeRate {
    return ExchangeRate(
        targetCurrency = currency.toCurrency(),
        baseCurrency = baseCurrency.toCurrency(),
        rate = rate,
        dateTimeUtc = ZonedDateTime.now()
    )
}