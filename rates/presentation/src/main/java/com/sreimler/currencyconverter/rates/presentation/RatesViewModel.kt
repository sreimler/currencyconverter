package com.sreimler.currencyconverter.rates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toExchangeRateUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
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

class RatesViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val _state = MutableStateFlow(RatesListState())
    val state = _state
        .onStart { getExchangeRates() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RatesListState())

    fun getExchangeRates() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isRefreshing = true) }

                val baseCurrency = currencyRepository.getBaseCurrency().map { currency ->
                    currency.toCurrencyUi()
                }
                val rates = currencyRepository.getLatestExchangeRates()
                val refreshDate = currencyRepository.getLastUpdateTime().map { timestamp ->
                    ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault()
                    )
                }
                _state.update {
                    it.copy(
                        exchangeRates = rates.map { exchangeRates ->
                            exchangeRates.map { it.toExchangeRateUi() }
                        },
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

    fun onCurrencyClicked(currencyUi: CurrencyUi) {
        viewModelScope.launch {
            currencyRepository.setSourceCurrency(currencyRepository.getCurrency(currencyUi.code).first())
        }
    }
}