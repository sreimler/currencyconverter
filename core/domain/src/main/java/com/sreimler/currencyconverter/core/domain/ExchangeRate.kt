package com.sreimler.currencyconverter.core.domain

import java.time.ZonedDateTime

data class ExchangeRate(
    val targetCurrency: Currency,
    val baseCurrency: Currency,
    val rate: Double,
    val dateTimeUtc: ZonedDateTime
)
