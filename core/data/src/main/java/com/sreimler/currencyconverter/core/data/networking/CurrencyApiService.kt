package com.sreimler.currencyconverter.core.data.networking

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("currencies")
    suspend fun getCurrencyList(@Query("apikey") key: String): Response<GetCurrenciesResponse>

    @GET("latest")
    suspend fun getExchangeRates(
        @Query("apikey") key: String,
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") currencies: String
    ): Response<GetExchangeRateResponse>
}