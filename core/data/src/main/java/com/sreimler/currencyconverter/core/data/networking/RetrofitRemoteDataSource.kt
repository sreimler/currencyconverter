package com.sreimler.currencyconverter.core.data.networking

import com.sreimler.currencyconverter.core.data.BuildConfig
import com.sreimler.currencyconverter.core.data.mappers.toCurrency
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.RemoteCurrencyDataSource
import timber.log.Timber
import java.time.ZonedDateTime
import java.util.Base64


class RetrofitRemoteDataSource(
    private val retrofitService: FreecurrencyApiService
) : RemoteCurrencyDataSource {

    val decodedApiKey = Base64.getDecoder().decode(BuildConfig.API_KEY_FREECURRENCY).decodeToString()

    override suspend fun getCurrencies(): List<Currency> {
        Timber.i("invoked")
        val response = retrofitService.getCurrencyList(decodedApiKey)
        Timber.i("getCurrencies() response: ${response.body()}")

        val currencies: MutableList<CurrencySerializable> = mutableListOf()
        response.body()?.data?.forEach {
            currencies.add(it.value)
        }
        return currencies.map { it.toCurrency() }
    }

    override suspend fun getExchangeRates(
        requestBaseCurrency: Currency,
        enabledCurrencies: List<Currency>
    ): List<ExchangeRate> {
        Timber.i("invoked")
        val currencies = enabledCurrencies.joinToString(separator = ",") {
            it.code
        }
        val response = retrofitService.getExchangeRates(
            decodedApiKey,
            baseCurrency = requestBaseCurrency.code,
            currencies = currencies
        )
        Timber.i("getExchangeRates() response: ${response.body()}")

        // TODO: extract update datetime from response headers?
        val dateTimeUtc = ZonedDateTime.now()

        return response.body()?.data?.mapNotNull { (code, rate) ->
            ExchangeRate(
                rateBaseCurrency = requestBaseCurrency,
                currency = enabledCurrencies.first { it.code == code },
                rate = rate,
                dateTimeUtc = dateTimeUtc
            )
        } ?: emptyList()
    }
}
