package com.sreimler.currencyconverter.core.data.networking

import com.sreimler.currencyconverter.core.data.BuildConfig
import com.sreimler.currencyconverter.core.data.mappers.toCurrency
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.RemoteCurrencyDataSource
import timber.log.Timber
import java.time.ZonedDateTime


class RetrofitRemoteDataSource(
    private val retrofitService: FreecurrencyApiService
) : RemoteCurrencyDataSource {

    override suspend fun getCurrencies(): List<Currency> {
        Timber.i("invoked")
        val response = retrofitService.getCurrencyList(BuildConfig.API_KEY_FREECURRENCY)
        Timber.i("getCurrencies() response: ${response.body()}")

        val currencies: MutableList<CurrencySerializable> = mutableListOf()
        response.body()?.data?.forEach {
            currencies.add(it.value)
        }
        return currencies.map { it.toCurrency() }
    }

    override suspend fun getExchangeRates(
        baseCurrency: Currency,
        enabledCurrencies: List<Currency>
    ): List<ExchangeRate> {
        Timber.i("invoked")
        val currencies = enabledCurrencies.joinToString(separator = ",") {
            it.code
        }
        val response = retrofitService.getExchangeRates(
            BuildConfig.API_KEY_FREECURRENCY,
            baseCurrency = baseCurrency.code,
            currencies = currencies
        )
        Timber.i("getExchangeRates() response: ${response.body()}")

        // TODO: extract update datetime from response headers?
        val dateTimeUtc = ZonedDateTime.now()

        return response.body()?.data?.mapNotNull { (code, rate) ->
            ExchangeRate(
                baseCurrency = baseCurrency,
                targetCurrency = enabledCurrencies.first { it.code == code },
                rate = rate,
                dateTimeUtc = dateTimeUtc
            )
        } ?: emptyList()
    }
}
