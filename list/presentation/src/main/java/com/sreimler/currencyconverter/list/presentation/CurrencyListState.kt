package com.sreimler.currencyconverter.list.presentation

import com.sreimler.currencyconverter.core.domain.CURRENCY_EUR
import com.sreimler.currencyconverter.core.domain.CURRENCY_USD
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import java.time.LocalDateTime


// TODO: check if this is best practice for handling ui state data and loading state
sealed interface CurrencyListState {
    data class Success(
        val exchangeRates: List<ExchangeRate>,
        val refreshDate: LocalDateTime = LocalDateTime.now(),
        val sourceCurrency: Currency = CURRENCY_USD,
        val targetCurrency: Currency = CURRENCY_EUR,
        val sourceAmount: Double = 0.0,
        val targetAmount: Double = 0.0
    ) : CurrencyListState

    data object Loading : CurrencyListState
    data object Error : CurrencyListState
}
