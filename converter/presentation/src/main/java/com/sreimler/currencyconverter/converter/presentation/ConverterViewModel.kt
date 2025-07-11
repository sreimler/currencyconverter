package com.sreimler.currencyconverter.converter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.converter.presentation.component.AmountField
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.util.AppError
import com.sreimler.currencyconverter.core.domain.util.AppResult
import com.sreimler.currencyconverter.core.domain.util.ErrorLogger
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrency
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.util.baseCurrencyFlow
import com.sreimler.currencyconverter.core.presentation.util.currenciesFlow
import com.sreimler.currencyconverter.core.presentation.util.exchangeRatesFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the currency conversion screen. Manages the state and business logic for currency conversion.
 *
 * @property currencyRepository The repository used to fetch and manage currency and exchange rate data.
 * @property errorLogger An `ErrorLogger` instance.
 */
class ConverterViewModel(
    private val currencyRepository: CurrencyRepository,
    private val errorLogger: ErrorLogger
) : ViewModel() {

    // StateFlow to hold the current state of the converter screen
    private val _state = MutableStateFlow(
        ConverterState(isLoading = true)
    )
    val state: StateFlow<ConverterState> = _state

    // SharedFlow to emit errors encountered during operations
    private val _errors = MutableSharedFlow<AppError>()
    val errors: SharedFlow<AppError> = _errors

    init {
        observeCurrenciesAndRates()
    }

    /**
     * Observes changes in base currency, exchange rates, and available currencies.
     * Combines these flows and updates the state accordingly.
     */
    private fun observeCurrenciesAndRates() {
        val baseCurrencyFlow: Flow<CurrencyUi?> = baseCurrencyFlow(currencyRepository, _errors, errorLogger)
        val exchangeRatesFlow: Flow<List<ExchangeRateUi>> = exchangeRatesFlow(currencyRepository, _errors, errorLogger)
        val currenciesFlow: Flow<List<CurrencyUi>> = currenciesFlow(currencyRepository, _errors, errorLogger)

        combine(baseCurrencyFlow, exchangeRatesFlow, currenciesFlow) { base, rates, currencies ->
            Timber.d("Converter received updated data")

            // Retrieve and update source currency
            val sourceResult = currencyRepository.sourceCurrencyStream().first { it !is AppResult.Loading }
            when (sourceResult) {
                is AppResult.Success -> {
                    _state.update { it.copy(sourceCurrency = sourceResult.data.toCurrencyUi()) }
                }

                is AppResult.Error -> {
                    if (sourceResult.error is AppError.InvalidRequest && base != null) {
                        // Source currency not set - fall back to default (base currency)
                        currencyRepository.setSourceCurrency(base.toCurrency())
                        _state.update { it.copy(sourceCurrency = base) }
                    } else {
                        errorLogger.log(sourceResult.error)
                        _errors.emit(sourceResult.error)
                        return@combine
                    }
                }

                is AppResult.Loading -> Unit
            }

            // Retrieve and update target currency
            val targetResult = currencyRepository.targetCurrencyStream().first { it !is AppResult.Loading }
            val fallback = getFallbackTargetCurrency(_state.value.sourceCurrency?.code, currencies)
            val resolvedTargetCurrency = when (targetResult) {
                is AppResult.Success -> {
                    // Check if source and target currencies are identical
                    val targetCurrency = targetResult.data.toCurrencyUi()
                    if (targetCurrency.code == _state.value.sourceCurrency?.code) {
                        fallback
                    } else {
                        targetCurrency
                    }
                }

                is AppResult.Error -> {
                    if (targetResult.error is AppError.InvalidRequest) {
                        // Target currency not set - fall back to default
                        fallback
                    } else {
                        errorLogger.log(targetResult.error)
                        _errors.emit(targetResult.error)
                        return@combine
                    }
                }

                is AppResult.Loading -> null // Should not happen due to filtering above
            }

            if (resolvedTargetCurrency != null) {
                _state.update { it.copy(targetCurrency = resolvedTargetCurrency) }
                Timber.d("Retrieved target currency")
            } else {
                errorLogger.log(AppError.NotFound)
                _errors.emit(AppError.NotFound)
                return@combine
            }

            // Update exchange rate and state
            val exchangeRate =
                calculateExchangeRate(_state.value.sourceCurrency, _state.value.targetCurrency, exchangeRates = rates)
            val targetAmount = 1.0 * exchangeRate

            _state.update {
                it.copy(
                    isLoading = false,
                    currencyList = currencies,
                    exchangeRateList = rates,
                    baseCurrency = base,
                    exchangeRate = exchangeRate,
                    targetAmount = targetAmount
                )
            }


        }
            .onEach { Timber.d("Updated state with latest rate and currency information - source ${_state.value.sourceCurrency?.code}, target ${_state.value.targetCurrency?.code}") }
            .launchIn(viewModelScope)
    }

    /**
     * Retrieves a fallback target currency based on a predefined order.
     *
     * @param sourceCurrencyCode The code of the source currency to exclude from fallback options.
     * @param currencies The list of available currencies.
     * @param fallbackOrder The order of fallback currencies to consider.
     * @return The fallback target currency, or null if none is found.
     */
    private fun getFallbackTargetCurrency(
        sourceCurrencyCode: String?,
        currencies: List<CurrencyUi>,
        fallbackOrder: List<String> = listOf("USD", "EUR")
    ): CurrencyUi? {
        return fallbackOrder
            .firstNotNullOfOrNull { code ->
                currencies.firstOrNull { it.code == code && it.code != sourceCurrencyCode }
            }
    }

    /**
     * Updates the state when the amount in the source or target field changes.
     *
     * @param field The field being updated (source or target).
     * @param amount The new amount entered by the user.
     */
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

    /**
     * Swaps the source and target currencies and updates the state accordingly.
     */
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

    /**
     * Updates the state when a currency is selected for either the source or target field.
     *
     * @param field The field being updated (source or target).
     * @param currency The selected currency.
     */
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

    /**
     * Calculates the exchange rate between the source and target currencies.
     *
     * @param sourceCurrency The source currency.
     * @param targetCurrency The target currency.
     * @param exchangeRates The list of available exchange rates.
     * @return The calculated exchange rate, or 0.0 if not found.
     */
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
