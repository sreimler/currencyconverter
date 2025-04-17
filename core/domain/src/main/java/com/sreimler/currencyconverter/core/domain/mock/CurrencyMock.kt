package com.sreimler.currencyconverter.core.domain.mock

import com.sreimler.currencyconverter.core.domain.Currency

object CurrencyMock {
    val CURRENCY_USD = Currency(
        code = "USD",
        name = "United States Dollar",
        symbolNative = "$",
        decimalDigits = 2,
        rounding = 0,
        symbol = "$",
        namePlural = "US Dollars",
        type = "fiat"
    )

    val CURRENCY_EUR = Currency(
        code = "EUR",
        name = "Euro",
        symbolNative = "€",
        decimalDigits = 2,
        rounding = 0,
        symbol = "€",
        namePlural = "Euros",
        type = "fiat"
    )

    val CURRENCY_GBP = Currency(
        code = "GBP",
        name = "British Pound",
        symbolNative = "£",
        decimalDigits = 2,
        rounding = 0,
        symbol = "£",
        namePlural = "British Pounds",
        type = "fiat"
    )

    val CURRENCY_JPY = Currency(
        code = "JPY",
        name = "Japanese Yen",
        symbolNative = "¥",
        decimalDigits = 0,
        rounding = 0,
        symbol = "¥",
        namePlural = "Japanese Yen",
        type = "fiat"
    )

    val CURRENCY_AUD = Currency(
        code = "AUD",
        name = "Australian Dollar",
        symbolNative = "A$",
        decimalDigits = 2,
        rounding = 0,
        symbol = "A$",
        namePlural = "Australian Dollars",
        type = "fiat"
    )

    val CURRENCY_CAD = Currency(
        code = "CAD",
        name = "Canadian Dollar",
        symbolNative = "C$",
        decimalDigits = 2,
        rounding = 0,
        symbol = "C$",
        namePlural = "Canadian Dollars",
        type = "fiat"
    )

    val CURRENCY_CHF = Currency(
        code = "CHF",
        name = "Swiss Franc",
        symbolNative = "CHF",
        decimalDigits = 2,
        rounding = 0,
        symbol = "CHF",
        namePlural = "Swiss Francs",
        type = "fiat"
    )

    val CURRENCY_CNY = Currency(
        code = "CNY",
        name = "Chinese Yuan",
        symbolNative = "¥",
        decimalDigits = 2,
        rounding = 0,
        symbol = "¥",
        namePlural = "Chinese Yuan",
        type = "fiat"
    )

    val CURRENCY_INR = Currency(
        code = "INR",
        name = "Indian Rupee",
        symbolNative = "₹",
        decimalDigits = 2,
        rounding = 0,
        symbol = "₹",
        namePlural = "Indian Rupees",
        type = "fiat"
    )

    val CURRENCY_NZD = Currency(
        code = "NZD",
        name = "New Zealand Dollar",
        symbolNative = "NZ$",
        decimalDigits = 2,
        rounding = 0,
        symbol = "NZ$",
        namePlural = "New Zealand Dollars",
        type = "fiat"
    )

    val CURRENCY_LIST = listOf(
        CURRENCY_USD,
        CURRENCY_EUR,
        CURRENCY_GBP,
        CURRENCY_JPY,
        CURRENCY_AUD,
        CURRENCY_CAD,
        CURRENCY_CHF,
        CURRENCY_CNY,
        CURRENCY_INR,
        CURRENCY_NZD
    )
}