package com.sreimler.currencyconverter.core.presentation.util

import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.util.AppError
import com.sreimler.currencyconverter.core.domain.util.AppResult
import com.sreimler.currencyconverter.core.domain.util.ErrorLogger
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toExchangeRateUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


/**
 * Creates a flow that emits the base currency as a `CurrencyUi` object.
 * Handles errors by emitting them to the provided `MutableSharedFlow`.
 *
 * @param currencyRepository The repository providing the base currency stream.
 * @param errors A shared flow to emit errors encountered during processing.
 * @param errorLogger An `ErrorLogger` instance.
 * @return A flow emitting the base currency as a `CurrencyUi` object or `null` if unavailable.
 */
fun baseCurrencyFlow(
    currencyRepository: CurrencyRepository,
    errors: MutableSharedFlow<AppError>,
    errorLogger: ErrorLogger
): Flow<CurrencyUi?> =
    currencyRepository.baseCurrencyStream()
        .onEach { Timber.d("baseCurrencyStream emitted: $it") }
        .onEach {
            if (it is AppResult.Error) {
                errorLogger.log(it.error)
                errors.emit(it.error)
            }
        }
        .map {
            when (it) {
                is AppResult.Success -> it.data.toCurrencyUi()
                else -> null
            }
        }

/**
 * Creates a flow that emits a list of available currencies as `CurrencyUi` objects.
 * Handles errors by emitting them to the provided `MutableSharedFlow`.
 *
 * @param currencyRepository The repository providing the currencies stream.
 * @param errors A shared flow to emit errors encountered during processing.
 * @param errorLogger An `ErrorLogger` instance.
 * @return A flow emitting a list of `CurrencyUi` objects or an empty list if unavailable.
 */
fun currenciesFlow(
    currencyRepository: CurrencyRepository,
    errors: MutableSharedFlow<AppError>,
    errorLogger: ErrorLogger
): Flow<List<CurrencyUi>> =
    currencyRepository.currenciesStream()
        .onEach { Timber.d("currenciesStream emitted: $it") }
        .onEach {
            if (it is AppResult.Error) {
                errorLogger.log(it.error)
                errors.emit(it.error)
            }
        }
        .map {
            when (it) {
                is AppResult.Success -> {
                    it.data.map { item ->
                        item.toCurrencyUi()
                    }
                }

                else -> listOf()
            }
        }

/**
 * Creates a flow that emits a list of exchange rates as `ExchangeRateUi` objects.
 * Combines the base currency stream with the latest exchange rates stream to calculate rates.
 * Handles errors by emitting them to the provided `MutableSharedFlow`.
 *
 * @param currencyRepository The repository providing the exchange rates and base currency streams.
 * @param errors A shared flow to emit errors encountered during processing.
 * @param errorLogger An `ErrorLogger` instance.
 * @return A flow emitting a list of `ExchangeRateUi` objects or an empty list if unavailable.
 */
fun exchangeRatesFlow(
    currencyRepository: CurrencyRepository,
    errors: MutableSharedFlow<AppError>,
    errorLogger: ErrorLogger
): Flow<List<ExchangeRateUi>> =
    currencyRepository.latestExchangeRatesStream()
        .onEach { Timber.d("exchangeRatesStream emitted: $it") }
        .onEach {
            if (it is AppResult.Error) {
                errorLogger.log(it.error)
                errors.emit(it.error)
            }
        }
        .combine(currencyRepository.baseCurrencyStream()) { ratesResult, baseResult ->
            Timber.d("Exchange rates: $ratesResult")
            Timber.d("Base currency in combine: $baseResult")
            val baseCurrency = (baseResult as? AppResult.Success)?.data
            val rates = (ratesResult as? AppResult.Success)?.data

            if (baseResult is AppResult.Loading || ratesResult is AppResult.Loading) {
                Timber.d("exchangeRatesFlow combine: One of the streams is Loading, returning emptyList()")
                return@combine emptyList()
            }
            if (baseResult is AppResult.Error) {
                errors.emit(baseResult.error)
                Timber.d("exchangeRatesFlow combine: baseResult is Error, returning emptyList()")
                return@combine emptyList()
            }
            if (ratesResult is AppResult.Error) {
                errors.emit(ratesResult.error)
                Timber.d("exchangeRatesFlow combine: ratesResult is Error, returning emptyList()")
                return@combine emptyList()
            }
            if (baseCurrency == null || rates.isNullOrEmpty()) {
                Timber.d("exchangeRatesFlow combine: baseCurrency is null or rates are empty, returning emptyList()")
                return@combine emptyList()
            }

            val requestBase = rates.first().rateBaseCurrency
            Timber.d("Updating rate list - base currency ${baseCurrency.code}, request base was ${requestBase.code}")
            if (requestBase != baseCurrency) {
                Timber.d("Converting rates...")
                val conversionRate = rates.find { it.currency == baseCurrency }?.rate ?: return@combine emptyList()
                rates.map { rate ->
                    rate.copy(
                        rateBaseCurrency = baseCurrency,
                        rate = rate.rate / conversionRate
                    )
                }
            } else {
                Timber.d("Returning unconverted rates")
                rates
            }
        }
        .map { list -> list.map { it.toExchangeRateUi() } }