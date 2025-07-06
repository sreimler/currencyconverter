package com.sreimler.currencyconverter.converter.presentation

import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi


/**
 * Represents the state of the currency converter in the UI.
 * Contains information about loading status, available currencies, exchange rates, selected currencies,
 * and the amounts for conversion.
 *
 * @property isLoading Indicates whether the data is currently being loaded.
 * @property currencyList A list of available currencies, represented as `CurrencyUi` objects.
 * @property exchangeRateList A list of exchange rates, represented as `ExchangeRateUi` objects.
 * @property baseCurrency The base currency used for conversions, represented as a `CurrencyUi` object.
 * @property sourceCurrency The source currency for conversion, represented as a `CurrencyUi` object.
 * @property targetCurrency The target currency for conversion, represented as a `CurrencyUi` object.
 * @property sourceAmount The amount in the source currency to be converted.
 * @property targetAmount The amount in the target currency after conversion.
 * @property exchangeRate The exchange rate used for the conversion.
 */
data class ConverterState(
    val isLoading: Boolean = true,
    val currencyList: List<CurrencyUi> = emptyList(),
    val exchangeRateList: List<ExchangeRateUi> = emptyList(),
    val baseCurrency: CurrencyUi? = null,
    val sourceCurrency: CurrencyUi? = null,
    val targetCurrency: CurrencyUi? = null,
    val sourceAmount: Double = 1.0,
    val targetAmount: Double = 1.0,
    val exchangeRate: Double = 0.0
)