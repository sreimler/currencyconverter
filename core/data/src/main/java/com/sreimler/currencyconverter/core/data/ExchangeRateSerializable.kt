package com.sreimler.currencyconverter.core.data

import kotlinx.serialization.Serializable

@Serializable
data class GetExchangeRateResponse(
    val data: Map<String, Double>
)

@Serializable
data class ExchangeRateSerializable(
    val currency: CurrencySerializable,
    val baseCurrency: CurrencySerializable,
    val rate: Double
)