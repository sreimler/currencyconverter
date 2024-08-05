package com.sreimler.currencyconverter.core.domain

import kotlinx.coroutines.flow.Flow

interface LocalCurrencyDataSource {
    suspend fun upsertCurrency(currency: Currency)
    suspend fun upsertCurrencies(currencies: List<Currency>)
    fun getCurrencies(): Flow<List<Currency>>
    fun getCurrency(symbol: String): Flow<Currency>
    suspend fun deleteCurrency(symbol: String)
    suspend fun deleteAllCurrencies()
}