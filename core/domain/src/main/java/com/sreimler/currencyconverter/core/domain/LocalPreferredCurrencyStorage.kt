package com.sreimler.currencyconverter.core.domain

import kotlinx.coroutines.flow.Flow


/**
 * Interface for local storage of currency preferences.
 * Provides methods to initialize and manage the base currency, as well as conversion source and target currency codes.
 */
interface LocalPreferredCurrencyStorage {

    /**
     * Initializes the DataStore with the default base currency if no value is already set.
     *
     * @param baseCurrencyCode The default base currency code to initialize with.
     */
    suspend fun initialize(baseCurrencyCode: String)

    /**
     * Retrieves the base currency code from the DataStore.
     * If no value is set, returns the default base currency.
     *
     * @return A Flow emitting the base currency code.
     */
    fun getBaseCurrencyCode(): Flow<String>

    /**
     * Retrieves the source currency code for conversions from the DataStore.
     *
     * @return A Flow emitting the source currency code, or null if not set.
     */
    fun getConversionSourceCurrency(): Flow<String?>

    /**
     * Retrieves the target currency code for conversions from the DataStore.
     *
     * @return A Flow emitting the target currency code, or null if not set.
     */
    fun getConversionTargetCurrency(): Flow<String?>

    /**
     * Stores the base currency code in the DataStore.
     *
     * @param currencyCode The base currency code to store.
     */
    suspend fun setBaseCurrencyCode(currencyCode: String)

    /**
     * Stores the source currency code for conversions in the DataStore.
     *
     * @param currencyCode The source currency code to store.
     */
    suspend fun setConversionSourceCurrency(currencyCode: String)


    /**
     * Stores the target currency code for conversions in the DataStore.
     *
     * @param currencyCode The target currency code to store.
     */
    suspend fun setConversionTargetCurrency(currencyCode: String)
}