package com.sreimler.currencyconverter.core.data.networking

import com.sreimler.currencyconverter.core.data.BuildConfig
import com.sreimler.currencyconverter.core.data.mappers.mapResponseToResult
import com.sreimler.currencyconverter.core.data.mappers.toCurrency
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.RemoteCurrencyDataSource
import com.sreimler.currencyconverter.core.domain.util.AppResult
import timber.log.Timber
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
import java.util.Base64


class RetrofitRemoteDataSource(
    private val retrofitService: FreecurrencyApiService
) : RemoteCurrencyDataSource {

    val decodedApiKey = Base64.getDecoder().decode(BuildConfig.API_KEY_FREECURRENCY).decodeToString()

    override suspend fun getCurrencies(): AppResult<List<Currency>> {
        Timber.i("invoked")
        val response = retrofitService.getCurrencyList(decodedApiKey)
        Timber.i("getCurrencies() response: ${response.body()}")

        return mapResponseToResult(response) {
            response.body()?.data?.values?.map { it.toCurrency() } ?: emptyList()
        }
    }

    override suspend fun getExchangeRates(
        requestBaseCurrency: Currency,
        enabledCurrencies: List<Currency>
    ): AppResult<List<ExchangeRate>> {
        Timber.i("invoked")
        val currencies = enabledCurrencies.joinToString(separator = ",") { it.code }
        val response = retrofitService.getExchangeRates(
            decodedApiKey,
            baseCurrency = requestBaseCurrency.code,
            currencies = currencies
        )
        Timber.i("getExchangeRates() response: ${response.body()}")

        // Extract update datetime from response headers
        val dateHeader = response.headers()["Date"]
        val dateTimeUtc = dateHeader?.let {
            ZonedDateTime.parse(it, RFC_1123_DATE_TIME)
        } ?: ZonedDateTime.now()

        val dateTimeUtc2 = ZonedDateTime.now()
        // TODO: verify if this works as expected
        Timber.i("Response datetime: $dateTimeUtc, system datetime: $dateTimeUtc2")

        val currencyMap = enabledCurrencies.associateBy { it.code }

        return mapResponseToResult(response) {
            response.body()?.data?.mapNotNull { (code, rate) ->
                currencyMap[code]?.let { currency ->
                    ExchangeRate(
                        rateBaseCurrency = requestBaseCurrency,
                        currency = currency,
                        rate = rate,
                        dateTimeUtc = dateTimeUtc
                    )
                }
            } ?: emptyList()
        }
    }
}
