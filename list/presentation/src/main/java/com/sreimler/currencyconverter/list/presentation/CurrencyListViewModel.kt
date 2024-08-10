package com.sreimler.currencyconverter.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.time.ZonedDateTime


class CurrencyListViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val _state = MutableStateFlow<CurrencyListState>(CurrencyListState.Loading)
    val state: StateFlow<CurrencyListState> = _state.asStateFlow()

    private val currencies = currencyRepository.getCurrencies()

    init {
        getExchangeRates()
    }

    private fun getExchangeRates() {
        viewModelScope.launch {
            try {
                val baseCurrency = currencyRepository.getBaseCurrency()
                val rates = currencyRepository.getLatestExchangeRates()
                _state.value = CurrencyListState.Success(
                    exchangeRates = rates,
                    refreshDate = ZonedDateTime.now().toLocalDateTime(), // TODO: insert actual refresh date
                    baseCurrency = baseCurrency
                )
            } catch (e: IOException) {
                Timber.e(e, "Was not able to get data from the api (check internet connection)")
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun amountChanged(changedAmount: String, amountCurrency: Currency) {
        //Log.i(
        //    CurrencyListViewModel::class.java.name, "Received new amount $changedAmount for currency " +
        //            amountCurrency.name
        //)
        //try {
        //    val newState = _state.value as CurrencyListState.Success
        //
        //    val userInput = changedAmount.trim().toDouble()
        //    val sourceRate = newState.exchangeRates.find { it.targetCurrency == newState.baseCurrency }!!
        //    val targetRate = newState.exchangeRates.find { it.targetCurrency == newState.targetCurrency }!!
        //
        //    if (amountCurrency == newState.baseCurrency) {
        //        // Calculate target currency
        //        val convertedAmount = (userInput * targetRate.rate) / sourceRate.rate
        //
        //        _state.update {
        //            newState.copy(
        //                sourceAmount = userInput,
        //                targetAmount = convertedAmount
        //            )
        //        }
        //    } else {
        //        // Calculate source currency
        //        val convertedAmount = (userInput * sourceRate.rate) / targetRate.rate
        //
        //        _state.update {
        //            newState.copy(
        //                sourceAmount = convertedAmount,
        //                targetAmount = userInput
        //            )
        //        }
        //    }
        //} catch (e: NumberFormatException) {
        //    // No valid double value
        //}
    }
}