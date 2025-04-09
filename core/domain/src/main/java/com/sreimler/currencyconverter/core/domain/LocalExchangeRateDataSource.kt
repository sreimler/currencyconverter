package com.sreimler.currencyconverter.core.domain

import kotlinx.coroutines.flow.Flow


/**
 * Local data source for exchange rates.
 */
interface LocalExchangeRateDataSource {
    // TODO: implement result class which will be returned here

    /**
     * Inserts an [ExchangeRate] into the local data source.
     */
    suspend fun insertExchangeRate(exchangeRate: ExchangeRate)

    /**
     * Inserts a list of [ExchangeRate]s into the local data source.
     */
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRate>)

    /**
     * Retrieves all available [ExchangeRate]s from the local data source.
     *
     * @return A [Flow] of a list of [ExchangeRate]s.
     */
    fun getAllExchangeRates(): Flow<List<ExchangeRate>>

    /**
     * Retrieves the latest [ExchangeRate] available for a [baseCurrency] from the local data source.
     *
     * @property baseCurrency The base currency of the pair.
     * @return A [Flow] of the latest [ExchangeRate] for the pair.
     */
    fun getLatestExchangeRates(baseCurrency: Currency): Flow<List<ExchangeRate>>

    /**
     * Retrieves the latest [ExchangeRate] available for a pair of [baseCurrency] and [targetCurrency] from the local
     * data source.
     *
     * @property baseCurrency The base currency of the pair.
     * @property targetCurrency The target currency of the pair.
     * @return A [Flow] of the latest [ExchangeRate] for the pair.
     */
    fun getLatestExchangeRate(baseCurrency: Currency, targetCurrency: Currency): Flow<ExchangeRate>

    /**
     * Retrieves the last update time for the exchange rates of a given base currency.
     *
     * This function returns a flow that emits the timestamp (in milliseconds since epoch)
     * of the last time the exchange rates for the specified base currency were updated.
     * The flow may emit updates if the last update time changes while the flow is active.
     *
     * @param baseCurrency The base currency for which to retrieve the last update time.
     * @return A flow emitting the timestamp of the last update for the given base currency.
     *         If no update time is available, the flow may emit 0 or handle the absence of data in some other appropriate way
     *         (depending on the underlying data source implementation - consider clarifying this behavior if relevant).
     */
    fun getLastUpdateTime(baseCurrency: Currency): Flow<Long>

    /**
     * Deletes all [ExchangeRate]s from the local data source.
     */
    suspend fun deleteAllExchangeRates()
}