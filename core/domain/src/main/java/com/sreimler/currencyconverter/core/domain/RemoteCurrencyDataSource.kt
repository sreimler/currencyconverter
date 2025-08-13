package com.sreimler.currencyconverter.core.domain

import com.sreimler.currencyconverter.core.domain.util.AppResult


/**
 * Remote data source for currencies and exchange rates.
 */
interface RemoteCurrencyDataSource {

    /**
     * Retrieves the latest currencies from the remote data source.
     *
     * @return A list of available [Currency]s.
     */
    suspend fun getCurrencies(): AppResult<List<Currency>>

    /**
     * Retrieves the latest exchange rates from the remote data source.
     *
     * @property requestBaseCurrency the base currency which the exchange rates are based on.
     * @property enabledCurrencies the list of enabled currencies which will be retrieved.
     * @return A list containing the latest [ExchangeRate]s.
     */
    suspend fun getExchangeRates(
        requestBaseCurrency: Currency,
        enabledCurrencies: List<Currency>
    ): AppResult<List<ExchangeRate>>
}
