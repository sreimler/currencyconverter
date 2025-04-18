package com.sreimler.currencyconverter.core.presentation.models

import androidx.annotation.DrawableRes
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.presentation.R
import com.sreimler.currencyconverter.core.presentation.util.getDrawableIdForCurrency

data class CurrencyUi(
    val symbol: String,
    val name: String,
    val symbolNative: String,
    val decimalDigits: Int = 0,
    val rounding: Int = 0,
    val code: String,
    val namePlural: String,
    val type: String,
    val isEnabled: Boolean = true,
    @DrawableRes val flagRes: Int = R.drawable.question_sign,
)

fun Currency.toCurrencyUi() = CurrencyUi(
    symbol = symbol,
    name = name,
    symbolNative = symbolNative,
    decimalDigits = decimalDigits,
    rounding = rounding,
    code = code,
    namePlural = namePlural,
    type = type,
    isEnabled = isEnabled,
    flagRes = getDrawableIdForCurrency(code)
)

fun CurrencyUi.toCurrency() = Currency(
    symbol = symbol,
    name = name,
    symbolNative = symbolNative,
    decimalDigits = decimalDigits,
    rounding = rounding,
    code = code,
    namePlural = namePlural,
    type = type,
    isEnabled = isEnabled
)