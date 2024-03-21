package com.sreimler.currencyconverter.data.model

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