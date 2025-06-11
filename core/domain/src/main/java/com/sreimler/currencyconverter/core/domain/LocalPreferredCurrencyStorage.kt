package com.sreimler.currencyconverter.core.domain

import kotlinx.coroutines.flow.Flow


/**
 * Local storage for the base currency as well as conversion source and target currency codes.
 */
interface LocalPreferredCurrencyStorage {
    suspend fun initialize()
    suspend fun setBaseCurrencyCode(currencyCode: String)
    suspend fun setConversionSourceCurrency(currencyCode: String)
    suspend fun setConversionTargetCurrency(currencyCode: String)
    suspend fun getBaseCurrencyCode(): Flow<String>
    suspend fun getConversionSourceCurrency(): String?
    suspend fun getConversionTargetCurrency(): String?
}