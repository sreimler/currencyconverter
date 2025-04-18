package com.sreimler.currencyconverter.core.domain

import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getCurrencies(): Flow<List<Currency>>
    suspend fun fetchCurrencies() // TODO: implement result type
    suspend fun getLatestExchangeRates(): Flow<List<ExchangeRate>>
    suspend fun fetchExchangeRates() // TODO: implement result type
    suspend fun setBaseCurrency(currency: Currency)
    suspend fun getBaseCurrency(): Flow<Currency>
    suspend fun setSourceCurrency(currency: Currency)
    suspend fun getSourceCurrency(): Flow<Currency?>
    suspend fun setTargetCurrency(currency: Currency)
    suspend fun getTargetCurrency(): Flow<Currency?>
    suspend fun getLastUpdateTime(): Flow<Long>
    suspend fun getCurrency(currencyCode: String): Flow<Currency>
}