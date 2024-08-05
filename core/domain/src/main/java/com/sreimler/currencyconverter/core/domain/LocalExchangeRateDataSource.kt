package com.sreimler.currencyconverter.core.domain

import kotlinx.coroutines.flow.Flow

interface LocalExchangeRateDataSource {
    // TODO: implement result class which will be returned here
    suspend fun insertExchangeRate(exchangeRate: ExchangeRate)
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRate>)
    fun getAllExchangeRates(): Flow<List<ExchangeRate>>
    fun getLatestExchangeRates(): Flow<List<ExchangeRate>>
    fun getLatestExchangeRate(baseCurrency: Currency, targetCurrency: Currency): Flow<ExchangeRate>
    suspend fun deleteAllExchangeRates()
}