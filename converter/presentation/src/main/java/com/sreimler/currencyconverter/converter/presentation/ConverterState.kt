package com.sreimler.currencyconverter.converter.presentation

import com.sreimler.currencyconverter.core.domain.Currency

data class ConverterState(
    val sourceCurrency: Currency? = null,
    val targetCurrency: Currency? = null
)
