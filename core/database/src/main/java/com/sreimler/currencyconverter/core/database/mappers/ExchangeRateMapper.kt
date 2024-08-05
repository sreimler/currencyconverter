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
        dateTimeUtc = Instant.parse(exchangeRateEntity.dateTimeUtc.toString()).atZone(ZoneId.of("UTC")),
        rate = exchangeRateEntity.rate
    )
}

fun ExchangeRate.toExchangeRateEntity(): ExchangeRateEntity {
    return ExchangeRateEntity(
        baseCurrencySymbol = baseCurrency.symbol,
        targetCurrencySymbol = targetCurrency.symbol,
        dateTimeUtc = dateTimeUtc.toInstant().toEpochMilli(),
        rate = rate
    )
}

//fun ExchangeRateEntity.toExchangeRate(): ExchangeRate {
//    return ExchangeRate(
//        baseCurrency = baseCurrencySymbol,
//        targetCurrency = ,
//        dateTimeUtc = Instant.parse(exchangeRateEntity.dateTimeUtc.toString()).atZone(ZoneId.of("UTC")),
//        rate = exchangeRateEntity.rate
//    )
//}