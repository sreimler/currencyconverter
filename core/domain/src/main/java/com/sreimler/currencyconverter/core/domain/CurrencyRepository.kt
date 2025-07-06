package com.sreimler.currencyconverter.core.domain

import com.sreimler.currencyconverter.core.domain.util.AppResult
import com.sreimler.currencyconverter.core.domain.util.EmptyAppResult
import kotlinx.coroutines.flow.Flow


/**
 * Repository interface for managing currency-related operations and data streams.
 * Provides methods to refresh data, retrieve currency streams, and set currency preferences.
 */
interface CurrencyRepository {

    /**
     * Retrieves a stream of available currencies.
     *
     * @return A `Flow` emitting `AppResult` containing a list of `Currency` objects.
     */
    fun currenciesStream(): Flow<AppResult<List<Currency>>>

    /**
     * Retrieves a stream of the latest exchange rates.
     *
     * @return A `Flow` emitting `AppResult` containing a list of `ExchangeRate` objects.
     */
    fun latestExchangeRatesStream(): Flow<AppResult<List<ExchangeRate>>>

    /**
     * Retrieves a stream of the base currency.
     *
     * @return A `Flow` emitting `AppResult` containing the base `Currency`.
     */
    fun baseCurrencyStream(): Flow<AppResult<Currency>>

    /**
     * Retrieves a stream of the source currency for conversions.
     *
     * @return A `Flow` emitting `AppResult` containing the source `Currency`.
     */
    fun sourceCurrencyStream(): Flow<AppResult<Currency>>

    /**
     * Retrieves a stream of the target currency for conversions.
     *
     * @return A `Flow` emitting `AppResult` containing the target `Currency`.
     */
    fun targetCurrencyStream(): Flow<AppResult<Currency>>

    /**
     * Retrieves a stream of the last update time for currency data.
     *
     * @return A `Flow` emitting `AppResult` containing the timestamp of the last update.
     */
    fun lastUpdateTimeStream(): Flow<AppResult<Long>>
//    fun getCurrency(currencyCode: String): Flow<AppResult<Currency>>

    /**
     * Sets the base currency for conversions.
     *
     * @param currency The `Currency` to set as the base currency.
     */
    suspend fun setBaseCurrency(currency: Currency)

    /**
     * Sets the target currency for conversions.
     *
     * @param currency The `Currency` to set as the target currency.
     */
    suspend fun setSourceCurrency(currency: Currency)

    /**
     * Sets the target currency for conversions.
     *
     * @param currency The `Currency` to set as the target currency.
     */
    suspend fun setTargetCurrency(currency: Currency)

    /**
     * Refreshes the list of available currencies and updates the local data source.
     *
     * @return An `EmptyAppResult` indicating the success or failure of the operation.
     */
    suspend fun refreshCurrencies(): EmptyAppResult

    /**
     * Refreshes the latest exchange rates and updates the local data source.
     *
     * @return An `EmptyAppResult` indicating the success or failure of the operation.
     */
    suspend fun refreshExchangeRates(): EmptyAppResult
}