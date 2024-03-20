package com.sreimler.currencyconverter.data.network

import com.sreimler.currencyconverter.data.model.Currencies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("currencies")
    suspend fun getCurrencies(@Query("apikey") key: String): Response<Currencies>
}