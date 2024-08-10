package com.sreimler.currencyconverter.core.data.networking

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Retrofit interface for the FreeCurrency API.
 */
interface FreecurrencyApiService {

    /**
     * Retrieves the list of available currencies.
     *
     * @return A [GetCurrenciesResponse] containing the list of currencies.
     */
    @GET("currencies")
    suspend fun getCurrencyList(@Query("apikey") key: String): Response<GetCurrenciesResponse>

    /**
     * Retrieves the latest exchange rates for a given base currency and a list of target currencies.
     *
     * @property baseCurrency The base currency for which to retrieve exchange rates.
     * @property currencies The list of target currencies for which to retrieve exchange rates.
     * @return A [GetExchangeRateResponse] containing the latest exchange rates.
     */
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("apikey") key: String,
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") currencies: String
    ): Response<GetExchangeRateResponse>
}