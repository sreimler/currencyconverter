package com.sreimler.currencyconverter.rates.presentation

import androidx.compose.runtime.Immutable
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import java.time.ZonedDateTime


/**
 * Represents the state of the rates list in the UI.
 * Contains information about the current refresh status, exchange rates, base currency, available currencies, and the last refresh date.
 *
 * @property isRefreshing Indicates whether the data is currently being refreshed.
 * @property exchangeRates A list of exchange rates displayed in the UI.
 * @property baseCurrency The base currency used for conversions, represented as a `CurrencyUi` object.
 * @property currencies A list of available currencies, represented as `CurrencyUi` objects.
 * @property refreshDate The timestamp of the last data refresh, represented as a `ZonedDateTime` object.
 */
@Immutable // Add immutable annotation to make sure that the unstable List does not cause frequent recompositions
data class RatesListState(
    val isRefreshing: Boolean = false,
    val exchangeRates: List<ExchangeRateUi> = emptyList(),
    val baseCurrency: CurrencyUi? = null,
    val currencies: List<CurrencyUi> = emptyList(),
    val refreshDate: ZonedDateTime? = null
)