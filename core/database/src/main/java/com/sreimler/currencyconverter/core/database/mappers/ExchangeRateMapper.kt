package com.sreimler.currencyconverter.core.database.mappers

import com.sreimler.currencyconverter.core.database.entity.ExchangeRateEntity
import com.sreimler.currencyconverter.core.database.entity.ExchangeRateWithCurrencyEntity
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import java.time.Instant
import java.time.ZoneId

fun ExchangeRate.toExchangeRateWithCurrencyEntity(): ExchangeRateWithCurrencyEntity {
    return ExchangeRateWithCurrencyEntity(
        baseCurrency = baseCurrency.toCurrencyEntity(),
        targetCurrency = targetCurrency.toCurrencyEntity(),
        exchangeRateEntity = this.toExchangeRateEntity()
    )
}

fun ExchangeRateWithCurrencyEntity.toExchangeRate(): ExchangeRate {
    return ExchangeRate(
        baseCurrency = baseCurrency.toCurrency(),
        targetCurrency = targetCurrency.toCurrency(),
        dateTimeUtc = Instant.ofEpochMilli(exchangeRateEntity.dateTimeUtc).atZone(ZoneId.of("UTC")),
        rate = exchangeRateEntity.rate
    )
}

fun ExchangeRate.toExchangeRateEntity(): ExchangeRateEntity {
    return ExchangeRateEntity(
        baseCurrencyCode = baseCurrency.code,
        targetCurrencyCode = targetCurrency.code,
        dateTimeUtc = dateTimeUtc.toInstant().toEpochMilli(),
        rate = rate
    )
}