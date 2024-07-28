package com.sreimler.currencyconverter.core.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetCurrenciesResponse(
    val data: Map<String, Currency>
)


@Serializable
data class Currency(
    val symbol: String,
    val name: String,
    @SerialName(value = "symbol_native") val symbolNative: String,
    @SerialName(value = "decimal_digits") val decimalDigits: Int,
    val rounding: Int,
    val code: String,
    @SerialName(value = "name_plural") val namePlural: String,
    val type: String
)