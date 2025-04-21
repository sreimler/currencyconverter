package com.sreimler.currencyconverter.converter.presentation

import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi

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