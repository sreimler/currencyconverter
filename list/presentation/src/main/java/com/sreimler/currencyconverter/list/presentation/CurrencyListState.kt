package com.sreimler.currencyconverter.list.presentation

import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


// TODO: check if this is best practice for handling ui state data and loading state
sealed interface CurrencyListState {
    data class Success(
        val exchangeRates: Flow<List<ExchangeRate>>,
        val refreshDate: LocalDateTime = LocalDateTime.now(),
        val baseCurrency: Flow<Currency?>,
        val sourceAmount: Double = 0.0,
        val targetAmount: Double = 0.0
    ) : CurrencyListState

    data object Loading : CurrencyListState
    data object Error : CurrencyListState
}
