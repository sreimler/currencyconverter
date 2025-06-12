package com.sreimler.currencyconverter.core.data

import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.LocalCurrencyDataSource
import com.sreimler.currencyconverter.core.domain.LocalExchangeRateDataSource
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import com.sreimler.currencyconverter.core.domain.RemoteCurrencyDataSource
import com.sreimler.currencyconverter.core.domain.policy.SyncPolicy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException


/**
 * Implementation of the [CurrencyRepository] interfaces.
 *
 * Provides access to the available currency and exchange rate data.
 * Also handles interactions between remote and local data sources.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstCurrencyRepository(
    private val localCurrencyDataSource: LocalCurrencyDataSource,
    private val localExchangeRateDataSource: LocalExchangeRateDataSource,
    private val remoteCurrencyDataSource: RemoteCurrencyDataSource,
    private val dataStoreCurrencyStorage: LocalPreferredCurrencyStorage
) : CurrencyRepository {

    /**
     * Gets the currencies from the local data source.
     */
    override fun getCurrencies(): Flow<List<Currency>> = flow {
        val localCurrencies = localCurrencyDataSource.getCurrencies().first()
        if (localCurrencies.isEmpty()) {
            fetchCurrencies()
            emit(localCurrencyDataSource.getCurrencies().first())
        } else {
            emit(localCurrencies)
        }
        Timber.d("invoked")
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

        // Fetch exchange rates if none available or if outdated
        if (rates.first().isEmpty()) {
            Timber.i("No exchange rates in local repository - fetching from remote")
            fetchExchangeRates()
        } else if (SyncPolicy.isDataStale(getLastUpdateTime().first())) {
            Timber.i("Local exchange rates are outdated - fetching from remote")
            fetchExchangeRates()
        }

        return rates
    }

    /**
     * Fetches the exchange rates from the remote data source and updates the local data source.
     */
    override suspend fun fetchExchangeRates() {
        Timber.d("invoked")

        // Prevent too frequent synchronizations
        if (!SyncPolicy.isRefreshAllowed(getLastUpdateTime().first())) {
            Timber.i("Refresh interval too short - will not fetch from remote")
            // TODO: handle this case, e.g., emit a Result.Error
            return
        } else {
            Timber.i("Fetching new rates from the api")
        }

        val baseCurrency = getBaseCurrency().first()
        val enabledCurrencies = getCurrencies().first().filter { it.isEnabled }
        val exchangeRates = remoteCurrencyDataSource.getExchangeRates(baseCurrency, enabledCurrencies)
        localExchangeRateDataSource.insertExchangeRates(exchangeRates)
    }

    /**
     * Sets the base currency in the local storage.
     *
     * @property currency The [Currency] to set as the base currency.
     */
    override suspend fun setBaseCurrency(currency: Currency) {
        dataStoreCurrencyStorage.setBaseCurrencyCode(currency.code)
    }

    /**
     * Gets the base currency from the local storage.
     *
     * @return A [Flow] of the base [Currency].
     */
    override suspend fun getBaseCurrency(): Flow<Currency> {
        Timber.d("invoked")
        return dataStoreCurrencyStorage.getBaseCurrencyCode().flatMapLatest { code ->
            localCurrencyDataSource.getCurrency(code).filterNotNull()
        }
    }

    override suspend fun setSourceCurrency(currency: Currency) {
        dataStoreCurrencyStorage.setConversionSourceCurrency(currency.code)
    }

    override suspend fun getSourceCurrency(): Flow<Currency?> {
        Timber.d("invoked")
        return flow {
            val sourceCurrencyCode = dataStoreCurrencyStorage.getConversionSourceCurrency() ?: ""
            if (sourceCurrencyCode == "") return@flow

            var sourceCurrency = localCurrencyDataSource.getCurrency(sourceCurrencyCode).firstOrNull()

            if (sourceCurrency != null) {
                emit(sourceCurrency)
            } else {
                try {
                    fetchCurrencies()
                    sourceCurrency = localCurrencyDataSource.getCurrency(sourceCurrencyCode).first()
                    if (sourceCurrency != null) {
                        emit(sourceCurrency)
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

    override suspend fun setTargetCurrency(currency: Currency) {
        dataStoreCurrencyStorage.setConversionTargetCurrency(currency.code)
    }

    override suspend fun getTargetCurrency(): Flow<Currency?> {
        return flow {
            val targetCurrencyCode = dataStoreCurrencyStorage.getConversionTargetCurrency() ?: ""
            if (targetCurrencyCode == "") return@flow

            var targetCurrency = localCurrencyDataSource.getCurrency(targetCurrencyCode).firstOrNull()

            if (targetCurrency != null) {
                emit(targetCurrency)
            } else {
                try {
                    fetchCurrencies()
                    targetCurrency = localCurrencyDataSource.getCurrency(targetCurrencyCode).first()
                    if (targetCurrency != null) {
                        emit(targetCurrency)
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

    override suspend fun getLastUpdateTime(): Flow<Long> {
        Timber.i("Last update: ${localExchangeRateDataSource.getLastUpdateTime(getBaseCurrency().first()).first()}")
        return localExchangeRateDataSource.getLastUpdateTime(getBaseCurrency().first())
    }

    override suspend fun getCurrency(currencyCode: String): Flow<Currency> {
        Timber.d("invoked")
        val currency = localCurrencyDataSource.getCurrency(currencyCode).first()
        if (currency == null) {
            Timber.i("no currencies available - fetching from remote")
            fetchCurrencies()
        }
        return localCurrencyDataSource.getCurrency(currencyCode).filterNotNull()
    }
}