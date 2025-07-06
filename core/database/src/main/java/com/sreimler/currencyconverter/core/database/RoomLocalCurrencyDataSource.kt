package com.sreimler.currencyconverter.core.database

import com.sreimler.currencyconverter.core.database.dao.CurrencyDao
import com.sreimler.currencyconverter.core.database.mappers.toCurrency
import com.sreimler.currencyconverter.core.database.mappers.toCurrencyEntity
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.LocalCurrencyDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber


class RoomLocalCurrencyDataSource(private val currencyDao: CurrencyDao) : LocalCurrencyDataSource {

    override fun getCurrencies(): Flow<List<Currency>> {
        return currencyDao.observeCurrencies().map { entities ->
            entities.map { it.toCurrency() }
        }
    }

    override fun getCurrency(code: String): Flow<Currency?> {
        Timber.v("Getting currency for $code")
        return currencyDao.observeCurrency(code).map { cur ->
            cur?.toCurrency()
        }
    }

    override suspend fun upsertCurrency(currency: Currency) {
        currencyDao.upsertCurrency(currency.toCurrencyEntity())
    }

    override suspend fun upsertCurrencies(currencies: List<Currency>) {
        currencyDao.upsertCurrencies(currencies.map { it.toCurrencyEntity() })
    }

    override suspend fun deleteCurrency(code: String) {
        currencyDao.deleteCurrency(code)
    }

    override suspend fun deleteAllCurrencies() {
        currencyDao.deleteAllCurrencies()
    }
}