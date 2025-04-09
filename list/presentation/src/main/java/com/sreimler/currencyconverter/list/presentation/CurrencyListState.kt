package com.sreimler.currencyconverter.list.presentation

import androidx.compose.runtime.Immutable
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.ZonedDateTime

// Add immutable annotation to make sure that the unstable List does not cause frequent recompositions
@Immutable
data class CurrencyListState(
    val isRefreshing: Boolean = false,
    val exchangeRates: Flow<List<ExchangeRate>> = flow { },
    val baseCurrency: Flow<Currency?> = flow { },
    val sourceAmount: Double = 0.0,
    val targetAmount: Double = 0.0,
    val refreshDate: Flow<ZonedDateTime> = flow { }
)