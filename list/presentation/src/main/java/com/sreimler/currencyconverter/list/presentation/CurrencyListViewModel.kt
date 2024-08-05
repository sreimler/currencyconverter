package com.sreimler.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.core.domain.CURRENCY_EUR
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.REFRESH_DATETIME
import com.sreimler.currencyconverter.list.presentation.CurrencyListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException


class CurrencyListViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val _state = MutableStateFlow<CurrencyListState>(CurrencyListState.Loading)
    val state: StateFlow<CurrencyListState> = _state.asStateFlow()

    init {
        getExchangeRates()

        //viewModelScope.launch {
        //    currencyUiState.collect { state ->
        //        when (state) {
        //            CurrencyUiState.Error -> Log.i(CurrencyConverterViewModel::class.java.name, "err")
        //            CurrencyUiState.Loading -> Log.i(CurrencyConverterViewModel::class.java.name, "loading..")
        //            is CurrencyUiState.Success -> Log.i(
        //                CurrencyConverterViewModel::class.java.name, "Received exchange rates:\n${state.exchangeRates}"
        //            )
        //        }
        //    }
        //}

    }

    private fun getExchangeRates() {
        viewModelScope.launch {
            try {
                val baseCurrency = currencyRepository.getBaseCurrency()
                val rates = currencyRepository.getExchangeRates(baseCurrency = baseCurrency)
                _state.value = CurrencyListState.Success(
                    exchangeRates = rates,
                    refreshDate = REFRESH_DATETIME, // insert actual refresh date
                    sourceCurrency = baseCurrency,
                    targetCurrency = CURRENCY_EUR
                )
            } catch (e: IOException) {
                Log.e(
                    CurrencyListViewModel::class.java.name,
                    "Was not able to get data from the api (check internet connection)",
                    e
                )
            }
        }
    }

    private fun getCurrencies() {
        // TODO: implement
    }

    fun amountChanged(changedAmount: String, amountCurrency: Currency) {
        Log.i(
            CurrencyListViewModel::class.java.name, "Received new amount $changedAmount for currency " +
                    amountCurrency.name
        )
        try {
            val newState = _state.value as CurrencyListState.Success

            val userInput = changedAmount.trim().toDouble()
            val sourceRate = newState.exchangeRates.find { it.targetCurrency == newState.sourceCurrency }!!
            val targetRate = newState.exchangeRates.find { it.targetCurrency == newState.targetCurrency }!!

            if (amountCurrency == newState.sourceCurrency) {
                // Calculate target currency
                val convertedAmount = (userInput * targetRate.rate) / sourceRate.rate

                _state.update {
                    newState.copy(
                        sourceAmount = userInput,
                        targetAmount = convertedAmount
                    )
                }
            } else {
                // Calculate source currency
                val convertedAmount = (userInput * sourceRate.rate) / targetRate.rate

                _state.update {
                    newState.copy(
                        sourceAmount = convertedAmount,
                        targetAmount = userInput
                    )
                }
            }
        } catch (e: NumberFormatException) {
            // No valid double value
        }
    }
}