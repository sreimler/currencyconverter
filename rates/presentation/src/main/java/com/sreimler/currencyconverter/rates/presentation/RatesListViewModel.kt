package com.sreimler.currencyconverter.rates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.util.AppError
import com.sreimler.currencyconverter.core.domain.util.AppResult
import com.sreimler.currencyconverter.core.domain.util.ErrorLogger
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrency
import com.sreimler.currencyconverter.core.presentation.util.baseCurrencyFlow
import com.sreimler.currencyconverter.core.presentation.util.currenciesFlow
import com.sreimler.currencyconverter.core.presentation.util.exchangeRatesFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


/**
 * ViewModel for managing the state and interactions of the rates list screen.
 * Observes currency and exchange rate data streams and updates the UI state accordingly.
 *
 * @property currencyRepository The repository providing currency and exchange rate data.
 * @property errorLogger An `ErrorLogger` instance.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RatesListViewModel(
    private val currencyRepository: CurrencyRepository,
    private val errorLogger: ErrorLogger
) : ViewModel() {

    // Holds the current state of the rates list screen
    private val _state = MutableStateFlow(RatesListState(isRefreshing = true))
    val state: StateFlow<RatesListState> = _state

    // Shared flow for emitting errors encountered during data processing
    private val _errors = MutableSharedFlow<AppError>()
    val errors: SharedFlow<AppError> = _errors

    init {
        observeCurrenciesAndRates()
    }

    /**
     * Observes currency and exchange rate streams and combines them into a single state.
     * Updates the UI state with the combined data.
     */
    private fun observeCurrenciesAndRates() {
        val baseCurrencyFlow: Flow<CurrencyUi?> = baseCurrencyFlow(currencyRepository, _errors, errorLogger)
        val exchangeRatesFlow: Flow<List<ExchangeRateUi>> = exchangeRatesFlow(currencyRepository, _errors, errorLogger)
        val currenciesFlow: Flow<List<CurrencyUi>> = currenciesFlow(currencyRepository, _errors, errorLogger)

        val refreshDateFlow: Flow<ZonedDateTime?> = currencyRepository.lastUpdateTimeStream()
            .onEach { Timber.d("refreshDateStream emitted: $it") }
            .onEach {
                if (it is AppResult.Error) {
                    errorLogger.log(it.error)
                    _errors.emit(it.error)
                }
            }
            .mapNotNull { (it as? AppResult.Success)?.data }
            .map { millis ->
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            }

        combine(
            baseCurrencyFlow,
            currenciesFlow,
            exchangeRatesFlow,
            refreshDateFlow,
            _state.map { it.isRefreshing }
        ) { base, currencies, rates, date, refreshing ->
            RatesListState(
                baseCurrency = base,
                currencies = currencies,
                exchangeRates = rates,
                refreshDate = date,
                isRefreshing = false
            )
        }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    /**
     * Refreshes the exchange rates by triggering a repository update.
     * Updates the UI state to indicate the refresh status.
     */
    fun updateExchangeRates() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            val refreshResult = currencyRepository.refreshExchangeRates()
            if (refreshResult is AppResult.Error) {
                errorLogger.log(refreshResult.error)
                _errors.emit(refreshResult.error)
            }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    /**
     * Handles a click event on a currency item.
     * Updates the conversion source and target currencies in the repository.
     *
     * @param targetCurrency The currency selected by the user.
     */
    fun onCurrencyClicked(targetCurrency: CurrencyUi) {
        Timber.i("Registered click on currency ${targetCurrency.code}")
        val baseCurrency = _state.value.baseCurrency

        viewModelScope.launch {
            if (baseCurrency == null) {
                val error = AppError.NotFound
                errorLogger.log(error)
                _errors.emit(error)
            } else {
                currencyRepository.setSourceCurrency(baseCurrency.toCurrency())
                currencyRepository.setTargetCurrency(targetCurrency.toCurrency())
            }
        }
    }

    /**
     * Handles a long-click event on a currency item.
     * Updates the base currency in the repository.
     *
     * @param currencyUi The currency selected by the user.
     */
    fun onCurrencyLongClicked(currencyUi: CurrencyUi) {
        Timber.i("Changing base currency to ${currencyUi.code}")
        viewModelScope.launch {
            currencyRepository.setBaseCurrency(currencyUi.toCurrency())
        }
    }
}
