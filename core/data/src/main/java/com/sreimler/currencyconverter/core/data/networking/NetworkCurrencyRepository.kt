package com.sreimler.currencyconverter.core.data.networking

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sreimler.currencyconverter.core.data.BuildConfig
import com.sreimler.currencyconverter.core.data.CurrencySerializable
import com.sreimler.currencyconverter.core.data.ExchangeRateSerializable
import com.sreimler.currencyconverter.core.data.toCurrency
import com.sreimler.currencyconverter.core.data.toCurrencySerializable
import com.sreimler.currencyconverter.core.data.toExchangeRate
import com.sreimler.currencyconverter.core.domain.CURRENCIES_LIST
import com.sreimler.currencyconverter.core.domain.CURRENCY_USD
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

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
        val currencies: MutableList<CurrencySerializable> = mutableListOf()
        response.body()?.data?.forEach {
            currencies.add(it.value)
        }
        Log.i(NetworkCurrencyRepository::class.java.name, "currencies: $currencies")
        return currencies.map { it.toCurrency() }
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

        val exchangeRates: MutableList<ExchangeRateSerializable> = mutableListOf()

        response.body()?.data?.forEach { (code, rate) ->
            CURRENCIES_LIST.find { currency -> currency.code == code }?.let {
                exchangeRates.add(
                    ExchangeRateSerializable(
                        currency = it.toCurrencySerializable(),
                        baseCurrency = baseCurrency.toCurrencySerializable(),
                        rate = rate
                    )
                )
            }
        }

        return exchangeRates.map { it.toExchangeRate() }
    }

    override fun getBaseCurrency(): Currency {
        // TODO: implement
        return CURRENCY_USD
    }

    override fun setBaseCurrency(currency: Currency) {
        // TODO: implement
    }
}