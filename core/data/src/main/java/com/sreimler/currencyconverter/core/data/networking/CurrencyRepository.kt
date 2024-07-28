package com.sreimler.currencyconverter.core.data.networking

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sreimler.currencyconverter.BuildConfig
import com.sreimler.currencyconverter.core.data.Currency
import com.sreimler.currencyconverter.core.data.ExchangeRate
import com.sreimler.currencyconverter.data.CURRENCIES_LIST
import com.sreimler.currencyconverter.data.CURRENCY_USD
import com.sreimler.currencyconverter.data.EXCHANGE_RATE_LIST
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface CurrencyRepository {
    suspend fun getCurrencies(): List<Currency>
    suspend fun getExchangeRates(baseCurrency: Currency): List<ExchangeRate>
    fun getBaseCurrency(): Currency
    fun setBaseCurrency(currency: Currency)
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
        val response = retrofitService.getCurrencyList(BuildConfig.API_KEY_FREECURRENCY)
        Log.i(NetworkCurrencyRepository::class.java.name, "response: ${response.body()}")
        val currencies: MutableList<Currency> = mutableListOf()
        response.body()?.data?.forEach {
            currencies.add(it.value)
        }
        Log.i(NetworkCurrencyRepository::class.java.name, "currencies: $currencies")
        return currencies
    }

    override suspend fun getExchangeRates(baseCurrency: Currency): List<ExchangeRate> {
        val currencies = CURRENCIES_LIST.joinToString(separator = ",") {
            it.code
        }

        val response = retrofitService.getExchangeRates(
            BuildConfig.API_KEY_FREECURRENCY,
            baseCurrency = baseCurrency.code,
            currencies = currencies
        )

        val exchangeRates: MutableList<ExchangeRate> = mutableListOf()

        response.body()?.data?.forEach { (code, rate) ->
            CURRENCIES_LIST.find { currency -> currency.code == code }?.let {
                exchangeRates.add(ExchangeRate(currency = it, baseCurrency = baseCurrency, rate = rate))
            }
        }

        return exchangeRates
    }

    override fun getBaseCurrency(): Currency {
        // TODO: implement
        return CURRENCY_USD
    }

    override fun setBaseCurrency(currency: Currency) {
        // TODO: implement
    }
}

class LocalCurrencyRepository : CurrencyRepository {
    override suspend fun getCurrencies(): List<Currency> {
        return CURRENCIES_LIST
    }

    override suspend fun getExchangeRates(baseCurrency: Currency): List<ExchangeRate> {
        return EXCHANGE_RATE_LIST
    }

    override fun getBaseCurrency(): Currency {
        return CURRENCY_USD
    }

    override fun setBaseCurrency(currency: Currency) {
        // TODO: implement
    }
}
