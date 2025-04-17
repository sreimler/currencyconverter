package com.sreimler.currencyconverter.converter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toExchangeRateUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConverterViewModel(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        ConverterState(isLoading = true)
    )
    val state: StateFlow<ConverterState> = _state

    init {
        // TODO: persist source and target currency and retrieve here
        getCurrenciesAndRates()
    }

    private fun getCurrenciesAndRates() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val currencies = currencyRepository.getCurrencies()
                    .map { it.map { it.toCurrencyUi() } }
                    .first()

                val baseCurrency = currencyRepository.getBaseCurrency()
                    .map { it.toCurrencyUi() }
                    .first()

                val exchangeRates = currencyRepository.getLatestExchangeRates()
                    .map { it.map { it.toExchangeRateUi() } }
                    .first()

                val targetCurrency = if (baseCurrency.code == "USD") {
                    currencies.firstOrNull { it.code == "EUR" } ?: currencies.first()
                } else {
                    currencies.firstOrNull { it.code == "USD" } ?: currencies.first()
                }

                val exchangeRate = calculateExchangeRate(baseCurrency, targetCurrency, exchangeRates)
                val targetAmount = 1.0 * exchangeRate

                _state.update {
                    it.copy(
                        isLoading = false,
                        currencyList = currencies,
                        exchangeRateList = exchangeRates,
                        baseCurrency = baseCurrency,
                        sourceCurrency = baseCurrency,
                        targetCurrency = targetCurrency,
                        exchangeRate = exchangeRate,
                        targetAmount = targetAmount
                    )
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onAmountChanged(field: AmountField, amount: String) {
        val parsed = amount.toDoubleOrNull() ?: return
        _state.update {
            when (field) {
                AmountField.SOURCE -> {
                    it.copy(
                        sourceAmount = parsed,
                        targetAmount = parsed * it.exchangeRate
                    )
                }

                AmountField.TARGET -> {
                    it.copy(
                        targetAmount = parsed,
                        sourceAmount = parsed / it.exchangeRate
                    )
                }
            }
        }
    }

    fun onCurrencySelected(field: AmountField, currency: CurrencyUi) {
        when (field) {
            AmountField.SOURCE -> {
                val exchangeRate =
                    calculateExchangeRate(currency, state.value.targetCurrency!!, state.value.exchangeRateList)
                _state.update {
                    it.copy(
                        sourceCurrency = currency,
                        exchangeRate = exchangeRate
                    )
                }
                onAmountChanged(AmountField.TARGET, state.value.targetAmount.toString())
            }

            AmountField.TARGET -> {
                val exchangeRate =
                    calculateExchangeRate(state.value.sourceCurrency!!, currency, state.value.exchangeRateList)
                _state.update {
                    it.copy(
                        targetCurrency = currency,
                        exchangeRate = exchangeRate
                    )
                }
                onAmountChanged(AmountField.SOURCE, state.value.sourceAmount.toString())
            }
        }
    }
}

private fun calculateExchangeRate(
    sourceCurrency: CurrencyUi,
    targetCurrency: CurrencyUi,
    exchangeRates: List<ExchangeRateUi>
): Double {
    val sourceRate = exchangeRates.find { it.targetCurrency == sourceCurrency }
    val targetRate = exchangeRates.find { it.targetCurrency == targetCurrency }

    return if (sourceRate != null && targetRate != null) {
        targetRate.rate / sourceRate.rate
    } else {
        0.0
    }
}