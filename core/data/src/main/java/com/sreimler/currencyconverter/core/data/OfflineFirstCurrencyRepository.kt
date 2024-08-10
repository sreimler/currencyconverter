package com.sreimler.currencyconverter.core.data

import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.LocalBaseCurrencyStorage
import com.sreimler.currencyconverter.core.domain.LocalCurrencyDataSource
import com.sreimler.currencyconverter.core.domain.LocalExchangeRateDataSource
import com.sreimler.currencyconverter.core.domain.RemoteCurrencyDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException


/**
 * Implementation of the [CurrencyRepository] interfaces.
 *
 * Provides access to the available currency and exchange rate data.
 * Also handles interactions between remote and local data sources.
 */
class OfflineFirstCurrencyRepository(
    private val localCurrencyDataSource: LocalCurrencyDataSource,
    private val localExchangeRateDataSource: LocalExchangeRateDataSource,
    private val remoteCurrencyDataSource: RemoteCurrencyDataSource,
    private val baseCurrencyStorage: LocalBaseCurrencyStorage
) : CurrencyRepository {

    /**
     * Gets the currencies from the local data source.
     */
    override fun getCurrencies(): Flow<List<Currency>> {
        Timber.d("invoked")
        return localCurrencyDataSource.getCurrencies()
    }

    /**
     * Fetches the currencies from the remote data source and updates the local data source.
     */
    override suspend fun fetchCurrencies() {
        Timber.d("invoked")
        val currencies = remoteCurrencyDataSource.getCurrencies()
        localCurrencyDataSource.upsertCurrencies(currencies)
    }

    /**
     * Gets the latest exchange rates from the local data source.
     */
    override suspend fun getLatestExchangeRates(): Flow<List<ExchangeRate>> {
        Timber.d("invoked")
        val rates = localExchangeRateDataSource.getLatestExchangeRates(getBaseCurrency().first())
        if (rates.first().isEmpty()) {
            fetchExchangeRates()
        }

        return rates
    }

    /**
     * Fetches the exchange rates from the remote data source and updates the local data source.
     */
    override suspend fun fetchExchangeRates() {
        Timber.d("invoked")
        val baseCurrency = getBaseCurrency().first()
        val enabledCurrencies = getCurrencies().first().filter { it.isEnabled }
        val exchangeRates = remoteCurrencyDataSource.getExchangeRates(baseCurrency, enabledCurrencies)
        localExchangeRateDataSource.insertExchangeRates(exchangeRates)
    }

    /**
     * Gets the base currency from the local storage.
     *
     * @return A [Flow] of the base [Currency].
     */
    override suspend fun getBaseCurrency(): Flow<Currency> {
        Timber.d("invoked")
        return flow {
            val baseCurrencyCode = baseCurrencyStorage.get()
            var baseCurrency = localCurrencyDataSource.getCurrency(baseCurrencyCode).firstOrNull()

            if (baseCurrency != null) {
                emit(baseCurrency)
            } else {
                try {
                    fetchCurrencies()
                    baseCurrency = localCurrencyDataSource.getCurrency(baseCurrencyCode).first()
                    if (baseCurrency != null) {
                        emit(baseCurrency)
                    }
                } catch (e: IOException) {
                    // TODO: handle network error, e.g., emit(Result.Error(e))
                    Timber.e(e)
                } catch (e: Error) {
                    // TODO: handle datasource error, e.g., emit(Result.Error(e))
                    Timber.e(e)
                }
            }
        }
    }

    /**
     * Sets the base currency in the local storage.
     *
     * @property currency The [Currency] to set as the base currency.
     */
    override suspend fun setBaseCurrency(currency: Currency) {
        baseCurrencyStorage.set(currency.symbol)
    }
}