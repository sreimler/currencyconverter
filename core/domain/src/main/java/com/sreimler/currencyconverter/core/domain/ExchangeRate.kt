package com.sreimler.currencyconverter.core.domain

import java.time.ZonedDateTime

data class ExchangeRate(
    val currency: Currency,
    val rateBaseCurrency: Currency,
    val rate: Double,
    val dateTimeUtc: ZonedDateTime
)
