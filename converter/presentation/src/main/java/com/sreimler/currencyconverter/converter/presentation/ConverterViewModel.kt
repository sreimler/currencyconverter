package com.sreimler.currencyconverter.converter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.converter.presentation.component.AmountField
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrency
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toExchangeRateUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class ConverterViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val _state = MutableStateFlow(
        ConverterState(isLoading = true)
    )
    val state: StateFlow<ConverterState> = _state

    fun refreshConversionState() {
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

                Timber.d(
                    "currencyRepository.getSourceCurrency(): ${
                        currencyRepository.getSourceCurrency().firstOrNull()
                    }"
                )

                val sourceCurrency =
                    currencyRepository.getSourceCurrency().firstOrNull()?.toCurrencyUi() ?: baseCurrency
                val targetCurrency: CurrencyUi = currencyRepository.getTargetCurrency().firstOrNull()?.toCurrencyUi()
                    ?: when (sourceCurrency.code) {
                        "USD" -> currencies.firstOrNull { it.code == "EUR" } ?: currencies.first()
                        else -> currencies.firstOrNull { it.code == "USD" } ?: currencies.first()
                    }

                Timber.d("sourceCurrency: $sourceCurrency, targetCurrency: $targetCurrency")
                val exchangeRate = calculateExchangeRate(sourceCurrency, targetCurrency, exchangeRates)
                val targetAmount = 1.0 * exchangeRate

                _state.update {
                    it.copy(
                        isLoading = false,
                        currencyList = currencies,
                        exchangeRateList = exchangeRates,
                        baseCurrency = baseCurrency,
                        sourceCurrency = sourceCurrency,
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

    fun onSwapCurrencies() {
        val sourceCurrency = state.value.sourceCurrency
        val targetCurrency = state.value.targetCurrency
        val exchangeRate = calculateExchangeRate(targetCurrency, sourceCurrency)

        _state.update {
            it.copy(
                sourceCurrency = targetCurrency,
                targetCurrency = sourceCurrency,
                exchangeRate = exchangeRate
            )
        }

        onAmountChanged(AmountField.SOURCE, state.value.sourceAmount.toString())
    }

    fun onCurrencySelected(field: AmountField, currency: CurrencyUi) {
        when (field) {
            AmountField.SOURCE -> {
                viewModelScope.launch {
                    currencyRepository.setSourceCurrency(currency.toCurrency())
                }
                val exchangeRate = calculateExchangeRate(currency, state.value.targetCurrency)
                _state.update {
                    it.copy(
                        sourceCurrency = currency,
                        exchangeRate = exchangeRate
                    )
                }
                onAmountChanged(AmountField.SOURCE, state.value.sourceAmount.toString())
            }

            AmountField.TARGET -> {
                viewModelScope.launch {
                    currencyRepository.setTargetCurrency(currency.toCurrency())
                }
                val exchangeRate = calculateExchangeRate(state.value.sourceCurrency, currency)
                _state.update {
                    it.copy(
                        targetCurrency = currency,
                        exchangeRate = exchangeRate
                    )
                }
                onAmountChanged(AmountField.TARGET, state.value.targetAmount.toString())
            }
        }
    }

    private fun calculateExchangeRate(
        sourceCurrency: CurrencyUi?,
        targetCurrency: CurrencyUi?,
        exchangeRates: List<ExchangeRateUi> = state.value.exchangeRateList
    ): Double {
        val sourceRate = exchangeRates.find { it.targetCurrency == sourceCurrency }
        val targetRate = exchangeRates.find { it.targetCurrency == targetCurrency }

        return if (sourceRate != null && targetRate != null) {
            Timber.d("calculating exchange rate to: ${sourceRate.rate / targetRate.rate}")
            targetRate.rate / sourceRate.rate
        } else {
            0.0
        }
    }
}