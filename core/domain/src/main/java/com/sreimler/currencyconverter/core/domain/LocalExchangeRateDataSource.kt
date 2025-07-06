package com.sreimler.currencyconverter.core.domain

import kotlinx.coroutines.flow.Flow


/**
 * Local data source for exchange rates.
 */
interface LocalExchangeRateDataSource {

    /**
     * Retrieves all available [ExchangeRate]s from the local data source.
     *
     * @return A [Flow] of a list of [ExchangeRate]s.
     */
    fun getAllExchangeRates(): Flow<List<ExchangeRate>>

    /**
     * Retrieves the latest [ExchangeRate]s available from the local data source.
     *
     * @return A [Flow] of the latest [ExchangeRate]s.
     */
    fun getLatestExchangeRates(): Flow<List<ExchangeRate>>

    /**
     * Retrieves the latest [ExchangeRate] available for a [targetCurrency] from the local data source.
     *
     * @property targetCurrency The target currency of the pair.
     * @return A [Flow] of the latest [ExchangeRate] for the pair.
     */
    fun getLatestExchangeRate(targetCurrency: Currency): Flow<ExchangeRate>

    /**
     * Retrieves the last update time for the exchange rates.
     *
     * This function returns a flow that emits the timestamp (in milliseconds since epoch)
     * of the last time the exchange rates were updated.
     * The flow may emit updates if the last update time changes while the flow is active.
     *
     * @return A flow emitting the timestamp of the last update. If no update time is available,
     *         the flow may emit 0 or handle the absence of data in some other appropriate way.
     */
    fun getLastUpdateTime(baseCurrency: Currency): Flow<Long>

    /**
     * Inserts an [ExchangeRate] into the local data source.
     */
    suspend fun insertExchangeRate(exchangeRate: ExchangeRate)

    /**
     * Inserts a list of [ExchangeRate]s into the local data source.
     */
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRate>)

    /**
     * Deletes all [ExchangeRate]s from the local data source.
     */
    suspend fun deleteAllExchangeRates()
}