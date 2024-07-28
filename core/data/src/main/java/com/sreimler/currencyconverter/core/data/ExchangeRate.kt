package com.sreimler.currencyconverter.core.data

import kotlinx.serialization.Serializable

@Serializable
data class GetExchangeRateResponse(
    val data: Map<String, Double>
)

@Serializable
data class ExchangeRate(
    val currency: Currency,
    val baseCurrency: Currency,
    val rate: Double
)