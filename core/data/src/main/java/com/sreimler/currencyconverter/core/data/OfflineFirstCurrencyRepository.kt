package com.sreimler.currencyconverter.core.data

import com.sreimler.currencyconverter.core.data.mappers.asAppResult
import com.sreimler.currencyconverter.core.data.mappers.toAppError
import com.sreimler.currencyconverter.core.data.mappers.toEmptyResult
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.LocalCurrencyDataSource
import com.sreimler.currencyconverter.core.domain.LocalExchangeRateDataSource
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import com.sreimler.currencyconverter.core.domain.RemoteCurrencyDataSource
import com.sreimler.currencyconverter.core.domain.policy.SyncPolicy
import com.sreimler.currencyconverter.core.domain.util.AppError
import com.sreimler.currencyconverter.core.domain.util.AppResult
import com.sreimler.currencyconverter.core.domain.util.EmptyAppResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.IOException


@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstCurrencyRepository(
    private val localCurrencyDataSource: LocalCurrencyDataSource,
    private val localExchangeRateDataSource: LocalExchangeRateDataSource,
    private val remoteCurrencyDataSource: RemoteCurrencyDataSource,
    private val dataStoreCurrencyStorage: LocalPreferredCurrencyStorage
) : CurrencyRepository {

    override fun currenciesStream(): Flow<AppResult<List<Currency>>> {
        return localCurrencyDataSource.getCurrencies().asAppResult()
    }

    /**
     * Emits the latest exchange rates for the current base currency.
     *
     * - Always emits the latest local rates as they change.
     * - On start, checks if data is missing or stale and triggers a remote refresh if needed.
     * - Emits [AppResult.Loading] while refreshing.
     * - Emits [AppResult.Error] if refresh fails and no local data is available.
     * - Remains reactive to all future local updates.
     */
    override fun latestExchangeRatesStream(): Flow<AppResult<List<ExchangeRate>>> {
        Timber.d("invoked")
        return baseCurrencyStream()
            .onEach { Timber.d("baseCurrencyStream emitted: $it") }
            .filterIsInstance<AppResult.Success<Currency>>() // Only proceed when base currency is loaded
            .map { it.data }
            .flatMapLatest { baseCurrency ->
                currenciesStream()
                    .filterIsInstance<AppResult.Success<List<Currency>>>() // Wait for currency list to load
                    .map { it.data.filter { it.isEnabled } }
                    .flatMapLatest { enabledCurrencies ->
                        localExchangeRateDataSource.getLatestExchangeRates().flatMapLatest { localRates ->
                            flow {
                                // Always emit local data immediately
                                emit(AppResult.Success(localRates.sortedBy { it.currency.name }))

                                val isStale = SyncPolicy.isDataStale(
                                    localExchangeRateDataSource.getLastUpdateTime(baseCurrency).first()
                                )
                                if (localRates.isEmpty() || isStale) {
                                    Timber.i("Local exchange rates not found / outdated - fetching from remote")

                                    // Try remote refresh
                                    val refreshResult = refreshExchangeRates()
                                    Timber.d("refresh result: $refreshResult")
                                    if (refreshResult is AppResult.Success) {
                                        // If successful, emit updated local data
                                        localExchangeRateDataSource.getLatestExchangeRates()
                                            .map { updatedRates ->
                                                AppResult.Success(updatedRates.sortedBy { it.currency.name })
                                            }
                                            .collect { emit(it) }
                                    } else if (refreshResult is AppResult.Error && localRates.isEmpty()) {
                                        emit(AppResult.Error(refreshResult.error))
                                    }
                                }
                            }
                        }
                    }
            }
    }

    override fun baseCurrencyStream(): Flow<AppResult<Currency>> =
        dataStoreCurrencyStorage.getBaseCurrencyCode()
            .distinctUntilChanged()
            .flatMapLatest { baseCurrencyCode ->
                flow {
                    emit(AppResult.Loading())
                    if (baseCurrencyCode.isBlank()) {
                        emit(AppResult.Error(AppError.NotFound))
                        return@flow
                    }
                    val currency = localCurrencyDataSource.getCurrency(baseCurrencyCode).firstOrNull()
                    if (currency == null) {
                        val result = refreshCurrencies()
                        if (result is AppResult.Error) {
                            emit(AppResult.Error(result.error))
                            return@flow
                        }
                    }
                    emitAll(
                        localCurrencyDataSource.getCurrency(baseCurrencyCode)
                            .map { it?.let { AppResult.Success(it) } ?: AppResult.Error(AppError.NotFound) }
                    )
                }
            }

    override fun sourceCurrencyStream(): Flow<AppResult<Currency>> =
        preferredCurrencyStream { dataStoreCurrencyStorage.getConversionSourceCurrency().first() }

    override fun targetCurrencyStream(): Flow<AppResult<Currency>> =
        preferredCurrencyStream { dataStoreCurrencyStorage.getConversionTargetCurrency().first() }

    override fun lastUpdateTimeStream(): Flow<AppResult<Long>> {
        Timber.i("invoked")
        return baseCurrencyStream()
            .filterIsInstance<AppResult.Success<Currency>>()
            .map { it.data }
            .flatMapLatest { baseCurrency ->
                localExchangeRateDataSource.getLastUpdateTime(baseCurrency)
            }
            .asAppResult()
    }

//    override fun getCurrency(currencyCode: String): Flow<AppResult<Currency>> {
//        Timber.d("invoked")
//        return flow {
//            Timber.d("Loading currency $currencyCode")
//            emit(AppResult.Loading())
//            val local = getCurrencySnapshot(currencyCode)
//            if (local == null) {
//                Timber.d("Hmm nothing found")
//                // Currencies might not be fetched yet - try to do so
//                try {
//                    Timber.d("refreshing currencies")
//                    val refreshResult = refreshCurrencies()
//                    // Only proceed with the lookup if refresh succeeded
//                    if (refreshResult is AppResult.Error) {
//                        emit(AppResult.Error(refreshResult.error))
//                        return@flow
//                    }
//                    Timber.d("Getting $currencyCode from refreshed")
//                    val fetched = getCurrencySnapshot(currencyCode)
//                    if (fetched != null) {
//                        // No need to keep emitting the currency since currency (base) data will be static
//                        Timber.d("SUCCESS!!")
//                        emit(AppResult.Success(fetched))
//                    } else {
//                        emit(AppResult.Error(AppError.NotFound))
//                    }
//                } catch (e: Exception) {
//                    emit(AppResult.Error(e.toAppError()))
//                }
//            }
//        }
//    }

    override suspend fun setBaseCurrency(currency: Currency) {
        dataStoreCurrencyStorage.setBaseCurrencyCode(currency.code)
    }

    override suspend fun setSourceCurrency(currency: Currency) {
        dataStoreCurrencyStorage.setConversionSourceCurrency(currency.code)
    }

    override suspend fun setTargetCurrency(currency: Currency) {
        dataStoreCurrencyStorage.setConversionTargetCurrency(currency.code)
    }

    /**
     * Fetches the currencies from the remote data source and updates the local data source.
     */
    override suspend fun refreshCurrencies(): EmptyAppResult {
        Timber.d("invoked")
        return try {
            val remoteResult = remoteCurrencyDataSource.getCurrencies()
            if (remoteResult is AppResult.Success) {
                localCurrencyDataSource.upsertCurrencies(remoteResult.data)
            }
            remoteResult.toEmptyResult()
        } catch (e: Exception) {
            AppResult.Error(e.toAppError())
        }
    }

    override suspend fun refreshExchangeRates(): EmptyAppResult {
        Timber.d("invoked")
        return try {
            val lastUpdateResult = lastUpdateTimeStream().firstOrNull()
            val lastUpdate = (lastUpdateResult as? AppResult.Success)?.data ?: 0L
            if (!SyncPolicy.isRefreshAllowed(lastUpdate)) {
                Timber.w("Refresh interval too short - will not fetch from remote")
                return AppResult.Error(AppError.InvalidRequest)
            }

            val baseResult = baseCurrencyStream().filterNot { it is AppResult.Loading }.first()
            Timber.d("Retrieving base currency from datastore: $baseResult")
            val baseCurrency = when (baseResult) {
                is AppResult.Success -> baseResult.data
                else -> return AppResult.Error(AppError.NotFound)
            }

            val currencyResult = currenciesStream().filterNot { it is AppResult.Loading }.first()
            Timber.d("Retrieving currencies from db: $currencyResult")
            val enabledCurrencies = when (currencyResult) {
                is AppResult.Success -> currencyResult.data.filter { it.isEnabled }
                is AppResult.Error -> throw RuntimeException("Failed to load currencies: ${currencyResult.error}")
                else -> throw IllegalStateException("Unexpected result while loading currencies.")
            }

            Timber.i("Fetching new rates from the API")
            val result = remoteCurrencyDataSource.getExchangeRates(baseCurrency, enabledCurrencies)
            if (result is AppResult.Success) {
                localExchangeRateDataSource.insertExchangeRates(result.data)
            }
            result.toEmptyResult()
        } catch (e: Exception) {
            AppResult.Error(e.toAppError())
        }
    }

    /**
     * Utility function that acts as a one time getter for the [Currency] with code [code] from local storage.
     */
    private suspend fun getCurrencySnapshot(code: String): Currency? =
        localCurrencyDataSource.getCurrency(code).firstOrNull()

    /**
     * Utility function that allows retrieval of flows of source or target currencies.
     * Besides different currency codes these are retrieved in the same way.
     */
    private fun preferredCurrencyStream(getCurrencyCode: suspend () -> String?): Flow<AppResult<Currency>> = flow {
        Timber.d("invoked")
        emit(AppResult.Loading())
        val currencyCode = getCurrencyCode() ?: ""
        if (currencyCode.isBlank()) {
            emit(AppResult.Error(AppError.InvalidRequest))
            return@flow
        }

        try {
            val currency = getCurrencySnapshot(currencyCode)
            if (currency != null) {
                emit(AppResult.Success(currency))
            } else {
                val refreshResult = refreshCurrencies()
                if (refreshResult is AppResult.Error) {
                    emit(AppResult.Error(refreshResult.error))
                    return@flow
                }
                val refreshedCurrency = getCurrencySnapshot(currencyCode)
                if (refreshedCurrency != null) {
                    emit(AppResult.Success(refreshedCurrency))
                } else {
                    emit(AppResult.Error(AppError.NotFound))
                }
            }
        } catch (e: IOException) {
            emit(AppResult.Error(AppError.NoInternet))
        }
    }
}