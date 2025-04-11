package com.sreimler.currencyconverter.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


class CurrencyListViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val _state = MutableStateFlow(CurrencyListState())
    val state = _state
        .onStart { getExchangeRates() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CurrencyListState())

//    private val currencies = currencyRepository.getCurrencies()

    fun getExchangeRates() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isRefreshing = true) }

                val baseCurrency = currencyRepository.getBaseCurrency()
                val rates = currencyRepository.getLatestExchangeRates()
                val refreshDate = currencyRepository.getLastUpdateTime().map { timestamp ->
                    ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault()
                    )
                }
                _state.update {
                    it.copy(
                    exchangeRates = rates,
                        refreshDate = refreshDate,
                    baseCurrency = baseCurrency
                    )
                }
            } catch (e: IOException) {
                Timber.e(e, "Was not able to get data from the api (check internet connection)")
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun updateExchangeRates() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isRefreshing = true) }
                currencyRepository.fetchExchangeRates()
            } catch (e: IOException) {
                Timber.e(e, "Was not able to get data from the api (check internet connection)")
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                delay(5000)
                _state.update { it.copy(isRefreshing = false) }
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