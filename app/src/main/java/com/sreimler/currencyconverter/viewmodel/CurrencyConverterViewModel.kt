package com.sreimler.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.data.model.ExchangeRate
import com.sreimler.currencyconverter.data.repository.LocalCurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface CurrencyUiState {
    data class Success(val exchangeRates: List<ExchangeRate>) : CurrencyUiState
    data object Loading : CurrencyUiState
    data object Error : CurrencyUiState
}

class CurrencyConverterViewModel : ViewModel() {
    private val currencyRepository = LocalCurrencyRepository()
    //private val currencyRepository = NetworkCurrencyRepository()

    var currencyUiState = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Loading)
        private set

    init {
        getExchangeRates()

        viewModelScope.launch {
            currencyUiState.collect { state ->
                when (state) {
                    CurrencyUiState.Error -> Log.i(CurrencyConverterViewModel::class.java.name, "err")
                    CurrencyUiState.Loading -> Log.i(CurrencyConverterViewModel::class.java.name, "loading..")
                    is CurrencyUiState.Success -> Log.i(
                        CurrencyConverterViewModel::class.java.name, "Received exchange rates:\n${state.exchangeRates}"
                    )
                }
            }
        }

    }

    private fun getExchangeRates() {
        viewModelScope.launch {
            try {
                val result = currencyRepository.getExchangeRates(baseCurrency = currencyRepository.getBaseCurrency())
                currencyUiState.value = CurrencyUiState.Success(result)
            } catch (e: IOException) {
                Log.e(
                    CurrencyConverterViewModel::class.java.name,
                    "Was not able to get data from the api (check internet connection)",
                    e
                )
            }
        }
    }

    private fun getCurrencies() {
        // TODO: implement
    }
}