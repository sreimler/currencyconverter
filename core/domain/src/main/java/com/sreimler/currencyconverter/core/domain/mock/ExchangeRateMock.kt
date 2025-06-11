package com.sreimler.currencyconverter.core.domain.mock

import com.sreimler.currencyconverter.core.domain.ExchangeRate
import java.time.ZonedDateTime

object ExchangeRateMock {
    val EXCHANGE_RATES = CurrencyMock.CURRENCY_LIST.map { targetCurrency ->
        ExchangeRate(
            rateBaseCurrency = CurrencyMock.CURRENCY_USD,
            currency = targetCurrency,
            rate = when (targetCurrency.code) {
                "USD" -> 1.0
                "EUR" -> 0.85
                "GBP" -> 0.75
                "JPY" -> 110.0
                "AUD" -> 1.35
                "CAD" -> 1.25
                "CHF" -> 0.92
                "CNY" -> 6.45
                "INR" -> 74.0
                "NZD" -> 1.4
                else -> 1.0 // Default rate for unknown currencies
            },
            dateTimeUtc = ZonedDateTime.now()
        )
    }
}