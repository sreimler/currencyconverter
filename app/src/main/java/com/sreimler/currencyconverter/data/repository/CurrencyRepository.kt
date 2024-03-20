package com.sreimler.currencyconverter.data.repository

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sreimler.currencyconverter.BuildConfig
import com.sreimler.currencyconverter.data.model.Currency
import com.sreimler.currencyconverter.data.network.CurrencyApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface CurrencyRepository {
    suspend fun getCurrencies(): List<Currency>
}

class NetworkCurrencyRepository : CurrencyRepository {
    private val baseUrl = "https://api.freecurrencyapi.com/v1/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: CurrencyApiService by lazy {
        retrofit.create(CurrencyApiService::class.java)
    }

    override suspend fun getCurrencies(): List<Currency> {
        val response = retrofitService.getCurrencies(BuildConfig.API_KEY_FREECURRENCY)
        Log.i(NetworkCurrencyRepository::class.java.name, "response: ${response.body()}")
        val currencies: MutableList<Currency> = mutableListOf()
        response.body()?.data?.forEach {
            currencies.add(it.value)
        }
        Log.i(NetworkCurrencyRepository::class.java.name, "currencies: $currencies")
        return currencies
    }
}
