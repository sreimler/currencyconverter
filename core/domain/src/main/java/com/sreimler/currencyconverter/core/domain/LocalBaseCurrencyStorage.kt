package com.sreimler.currencyconverter.core.domain


/**
 * Local storage for the base currency code.
 */
interface LocalBaseCurrencyStorage {

    /**
     * Get the base currency code.
     */
    suspend fun get(): String

    /**
     * Set the base currency code.
     *
     * @property currencyCode The currency code to set.
     */
    suspend fun set(currencyCode: String)
}