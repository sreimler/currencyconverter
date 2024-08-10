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
     * Deletes all [ExchangeRate]s from the local data source.
     */
    suspend fun deleteAllExchangeRates()
}