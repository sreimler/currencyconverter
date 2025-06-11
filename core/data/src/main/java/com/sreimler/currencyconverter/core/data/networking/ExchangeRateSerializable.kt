@file:OptIn(InternalSerializationApi::class)

package com.sreimler.currencyconverter.core.data.networking

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

typealias Code = String
typealias Rate = Double


/**
 * Response object for the GET exchange rate request.
 */
@Serializable
data class GetExchangeRateResponse(
    val data: Map<Code, Rate>
)

/**
 * Serializable representation of an exchange rate retrieved from the API.
 */
@Serializable
data class ExchangeRateSerializable(
    val currencySerializable: CurrencySerializable,
    val rateBaseCurrencySerializable: CurrencySerializable,
    val rate: Rate
)