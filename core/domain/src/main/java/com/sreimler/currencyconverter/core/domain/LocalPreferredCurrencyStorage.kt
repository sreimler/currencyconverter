package com.sreimler.currencyconverter.core.domain


/**
 * Local storage for the base currency (which rates depend on) as well as conversion source and target currency codes.
 */
interface LocalPreferredCurrencyStorage {
    suspend fun setBaseCurrency(currencyCode: String)
    suspend fun setSourceCurrency(currencyCode: String)
    suspend fun setTargetCurrency(currencyCode: String)
    suspend fun getBaseCurrency(): String
    suspend fun getSourceCurrency(): String?
    suspend fun getTargetCurrency(): String?
}