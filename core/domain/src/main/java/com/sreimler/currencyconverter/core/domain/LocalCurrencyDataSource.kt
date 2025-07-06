package com.sreimler.currencyconverter.core.domain

import kotlinx.coroutines.flow.Flow


/**
 * Local data source for currencies.
 */
interface LocalCurrencyDataSource {

    /**
     * Retrieves all available [Currency]s from the local data source.
     *
     * @return A [Flow] of a list of [Currency]s.
     */
    fun getCurrencies(): Flow<List<Currency>>

    /**
     * Retrieves a single [Currency] by its [code] from the local data source.
     *
     * @return A [Flow] of nullable [Currency].
     */
    fun getCurrency(code: String): Flow<Currency?>

    /**
     * Upserts a [Currency] in the local data source.
     *
     * @property currency The [Currency] to upsert.
     */
    suspend fun upsertCurrency(currency: Currency)

    /**
     * Upserts a list of [Currency]s in the local data source.
     *
     * @property currencies The list of [Currency]s to upsert.
     */
    suspend fun upsertCurrencies(currencies: List<Currency>)

    /**
     * Deletes a [Currency] by its [code] from the local data source.
     *
     * @property code The [Currency] code to delete.
     */
    suspend fun deleteCurrency(code: String)

    /**
     * Deletes all [Currency]s from the local data source.
     */
    suspend fun deleteAllCurrencies()
}