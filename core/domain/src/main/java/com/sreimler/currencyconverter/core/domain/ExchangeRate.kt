package com.sreimler.currencyconverter.core.domain

data class ExchangeRate(
    val currency: Currency,
    val baseCurrency: Currency,
    val rate: Double
)
