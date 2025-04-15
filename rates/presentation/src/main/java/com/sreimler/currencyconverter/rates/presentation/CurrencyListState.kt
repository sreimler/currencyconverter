package com.sreimler.currencyconverter.rates.presentation

import androidx.compose.runtime.Immutable
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.ZonedDateTime

// Add immutable annotation to make sure that the unstable List does not cause frequent recompositions
@Immutable
data class CurrencyListState(
    val isRefreshing: Boolean = false,
    val exchangeRates: Flow<List<ExchangeRateUi>> = flow { },
    val baseCurrency: Flow<CurrencyUi> = flow { },
    val sourceAmount: Double = 0.0,
    val targetAmount: Double = 0.0,
    val refreshDate: Flow<ZonedDateTime> = flow { }
)