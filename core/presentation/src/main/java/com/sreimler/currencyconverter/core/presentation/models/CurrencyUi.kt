package com.sreimler.currencyconverter.core.presentation.models

import androidx.annotation.DrawableRes
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.presentation.R
import com.sreimler.currencyconverter.core.presentation.util.getDrawableIdForCurrency

data class CurrencyUi(
    val name: String,
    val symbolNative: String,
    val decimalDigits: Int = 0,
    val code: String,
    val isEnabled: Boolean = true,
    @DrawableRes val flagRes: Int = R.drawable.question_sign,
)

fun Currency.toCurrencyUi() = CurrencyUi(
    name = name,
    symbolNative = symbolNative,
    decimalDigits = decimalDigits,
    code = code,
    isEnabled = isEnabled,
    flagRes = getDrawableIdForCurrency(code)
)