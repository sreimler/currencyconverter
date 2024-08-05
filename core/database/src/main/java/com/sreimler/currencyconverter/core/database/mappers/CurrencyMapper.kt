package com.sreimler.currencyconverter.core.database.mappers

import com.sreimler.currencyconverter.core.database.entity.CurrencyEntity
import com.sreimler.currencyconverter.core.domain.Currency

fun Currency.toCurrencyEntity(): CurrencyEntity {
    return CurrencyEntity(
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

fun CurrencyEntity.toCurrency(): Currency {
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