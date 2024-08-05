package com.sreimler.currencyconverter.core.data.mappers

import com.sreimler.currencyconverter.core.data.networking.CurrencySerializable
import com.sreimler.currencyconverter.core.domain.Currency

fun Currency.toCurrencySerializable(): CurrencySerializable {
    return CurrencySerializable(
        symbol = symbol,
        name = name,
        symbolNative = symbolNative,
        decimalDigits = decimalDigits,
        rounding = rounding,
        code = code,
        namePlural = namePlural,
        type = type
    )
}

fun CurrencySerializable.toCurrency(): Currency {
    return Currency(
        symbol = symbol,
        name = name,
        symbolNative = symbolNative,
        decimalDigits = decimalDigits,
        rounding = rounding,
        code = code,
        namePlural = namePlural,
        type = type
    )
}