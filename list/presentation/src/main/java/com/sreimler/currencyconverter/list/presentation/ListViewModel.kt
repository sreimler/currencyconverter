package com.sreimler.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.data.CURRENCY_EUR
import com.sreimler.currencyconverter.data.CURRENCY_USD
import com.sreimler.currencyconverter.data.REFRESH_DATETIME
import com.sreimler.currencyconverter.data.model.Currency
import com.sreimler.currencyconverter.data.model.ExchangeRate
import com.sreimler.currencyconverter.data.repository.LocalCurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime

// TODO: check if this is best practice for handling ui state data and loading state
sealed interface CurrencyUiState {
    data class Success(
        val exchangeRates: List<ExchangeRate>,
        val refreshDate: LocalDateTime = LocalDateTime.now(),
        val sourceCurrency: Currency = CURRENCY_USD,
        val targetCurrency: Currency = CURRENCY_USD,
        val sourceAmount: Double = 0.0,
        val targetAmount: Double = 0.0
    ) : CurrencyUiState

    data object Loading : CurrencyUiState
    data object Error : CurrencyUiState
}

class CurrencyListViewModel : ViewModel() {
    private val currencyRepository = LocalCurrencyRepository()
    //private val currencyRepository = NetworkCurrencyRepository()
    private val _currencyUiState = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Loading)
    val currencyUiState: StateFlow<CurrencyUiState> = _currencyUiState.asStateFlow()

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
                _currencyUiState.value = CurrencyUiState.Success(
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
            val uiState = _currencyUiState.value as CurrencyUiState.Success

            val userInput = changedAmount.trim().toDouble()
            val sourceRate = uiState.exchangeRates.find { it.currency == uiState.sourceCurrency }!!
            val targetRate = uiState.exchangeRates.find { it.currency == uiState.targetCurrency }!!

            if (amountCurrency == uiState.sourceCurrency) {
                // Calculate target currency
                val convertedAmount = (userInput * targetRate.rate) / sourceRate.rate

                _currencyUiState.update {
                    uiState.copy(
                        sourceAmount = userInput,
                        targetAmount = convertedAmount
                    )
                }
            } else {
                // Calculate source currency
                val convertedAmount = (userInput * sourceRate.rate) / targetRate.rate

                _currencyUiState.update {
                    uiState.copy(
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