package com.sreimler.currencyconverter.rates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrency
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toExchangeRateUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
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

@OptIn(ExperimentalCoroutinesApi::class)
class RatesViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val _state = MutableStateFlow(RatesListState())
    val state = _state
        .onStart { getExchangeRates() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RatesListState())

    fun getExchangeRates() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isRefreshing = true) }
                Timber.i("getting them base currencies")
                val baseCurrency = currencyRepository.getBaseCurrency().map { currency ->
                    currency.toCurrencyUi()
                }

                val rates = currencyRepository.getBaseCurrency()
                    .flatMapLatest { baseCurrency ->
                        val ratesFlow = currencyRepository.getLatestExchangeRates()
                        val rateList = ratesFlow.first()
                        var currencies = mutableListOf<String>()
                        rateList.map {
                            currencies.add("\"${it.currency.code}\"")
                        }
                        Timber.i("Currencies: $currencies")
                        val requestBase = rateList[0].rateBaseCurrency
                        Timber.d("Updating rate list - base currency ${baseCurrency.code}, request base was ${requestBase.code}")
                        if (requestBase != baseCurrency) {
                            Timber.d("Converting rates...")
                            val conversionRate = rateList.find { rate ->
                                rate.rateBaseCurrency == baseCurrency
                            }

                            ratesFlow.map { list ->
                                list.map { rate ->
                                    rate.copy(rateBaseCurrency = baseCurrency, rate = rate.rate / conversionRate!!.rate)
                                }
                            }
                        } else {
                            Timber.d("Returning unconverted rates")
                            ratesFlow
                        }
                    }

                val refreshDate = currencyRepository.getLastUpdateTime().map { timestamp ->
                    ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault()
                    )
                }
                Timber.d("Updating exchange rate ui list")
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
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun onCurrencyClicked(currencyUi: CurrencyUi) {
        viewModelScope.launch {
            currencyRepository.setSourceCurrency(currencyRepository.getCurrency(currencyUi.code).first())
            currencyRepository.setTargetCurrency(currencyRepository.getBaseCurrency().first())
        }
    }

    fun onCurrencyLongClicked(currencyUi: CurrencyUi) {
        Timber.i("Changing base currency to ${currencyUi.code}")
        viewModelScope.launch {
            currencyRepository.setBaseCurrency(currencyUi.toCurrency())
        }
    }
}