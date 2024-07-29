package com.sreimler.currencyconverter.core.domain

interface CurrencyRepository {
    suspend fun getCurrencies(): List<Currency>
    suspend fun getExchangeRates(baseCurrency: Currency): List<ExchangeRate>
    fun getBaseCurrency(): Currency
    fun setBaseCurrency(currency: Currency)
}