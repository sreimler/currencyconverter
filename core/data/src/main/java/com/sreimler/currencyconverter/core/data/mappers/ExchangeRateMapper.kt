package com.sreimler.currencyconverter.core.data.mappers

import com.sreimler.currencyconverter.core.data.networking.ExchangeRateSerializable
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import java.time.Instant
import java.time.ZoneId

fun ExchangeRate.toExchangeRateSerializable(): ExchangeRateSerializable {
    return ExchangeRateSerializable(
        currency = targetCurrency.toCurrencySerializable(),
        baseCurrency = baseCurrency.toCurrencySerializable(),
        rate = rate,
        dateTimeUtc = dateTimeUtc.toInstant().toEpochMilli()
    )
}

fun ExchangeRateSerializable.toExchangeRate(): ExchangeRate {
    return ExchangeRate(
        targetCurrency = currency.toCurrency(),
        baseCurrency = baseCurrency.toCurrency(),
        rate = rate,
        dateTimeUtc = Instant.parse(dateTimeUtc.toString()).atZone(ZoneId.of("UTC")),
    )
}