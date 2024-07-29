package com.sreimler.currencyconverter.core.domain

import java.time.LocalDateTime

val CURRENCY_EUR = Currency(
    symbol = "€",
    name = "Euro",
    symbolNative = "€",
    decimalDigits = 2,
    rounding = 0,
    code = "EUR",
    namePlural = "Euros",
    type = "fiat"
)

val CURRENCY_USD = Currency(
    symbol = "$",
    name = "US Dollar",
    symbolNative = "$",
    decimalDigits = 2,
    rounding = 0,
    code = "USD",
    namePlural = "US dollars",
    type = "fiat"
)

val CURRENCY_CAD = Currency(
    symbol = "CA$",
    name = "Canadian Dollar",
    symbolNative = "$",
    decimalDigits = 2,
    rounding = 0,
    code = "CAD",
    namePlural = "Canadian dollars",
    type = "fiat"
)

val CURRENCY_JPY = Currency(
    symbol = "¥",
    name = "Japanese Yen",
    symbolNative = "¥",
    decimalDigits = 0,
    rounding = 0,
    code = "JPY",
    namePlural = "Japanese yen",
    type = "fiat"
)

val CURRENCIES_LIST = listOf(
    CURRENCY_EUR,
    CURRENCY_USD,
    CURRENCY_JPY
)

val EXCHANGE_RATE_LIST = listOf(
    ExchangeRate(currency = CURRENCY_CAD, baseCurrency = CURRENCY_USD, rate = 1.3470202542),
    ExchangeRate(currency = CURRENCY_EUR, baseCurrency = CURRENCY_USD, rate = 0.9142101729),
    ExchangeRate(currency = CURRENCY_USD, baseCurrency = CURRENCY_USD, rate = 1.0),
    ExchangeRate(currency = CURRENCY_JPY, baseCurrency = CURRENCY_USD, rate = 150.7430869771)
)

val REFRESH_DATETIME: LocalDateTime = LocalDateTime.parse("2024-03-22T21:00:00")