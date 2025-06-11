package com.sreimler.currencyconverter.core.data.mappers

import com.sreimler.currencyconverter.core.data.networking.ExchangeRateSerializable
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import java.time.ZonedDateTime

fun ExchangeRate.toExchangeRateSerializable(): ExchangeRateSerializable {
    return ExchangeRateSerializable(
        currencySerializable = currency.toCurrencySerializable(),
        rateBaseCurrencySerializable = rateBaseCurrency.toCurrencySerializable(),
        rate = rate,
    )
}

fun ExchangeRateSerializable.toExchangeRate(): ExchangeRate {
    return ExchangeRate(
        currency = currencySerializable.toCurrency(),
        rateBaseCurrency = rateBaseCurrencySerializable.toCurrency(),
        rate = rate,
        dateTimeUtc = ZonedDateTime.now()
    )
}