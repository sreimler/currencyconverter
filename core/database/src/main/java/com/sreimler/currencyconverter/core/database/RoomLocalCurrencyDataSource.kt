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

    override suspend fun upsertCurrency(currency: Currency) {
        currencyDao.upsertCurrency(currency.toCurrencyEntity())
    }

    override suspend fun upsertCurrencies(currencies: List<Currency>) {
        currencyDao.upsertCurrencies(currencies.map { it.toCurrencyEntity() })
    }

    override fun getCurrencies(): Flow<List<Currency>> {
        return currencyDao.getCurrencies().map { entities ->
            entities.map { it.toCurrency() }
        }
    }

    override suspend fun getCurrency(code: String): Flow<Currency?> {
        Timber.v("Getting currency for $code")
        return currencyDao.getCurrency(code).map { cur ->
            cur?.toCurrency()
        }
    }

    override suspend fun deleteCurrency(code: String) {
        currencyDao.deleteCurrency(code)
    }

    override suspend fun deleteAllCurrencies() {
        currencyDao.deleteAllCurrencies()
    }

    override suspend fun setEnabled(currency: Currency, isEnabled: Boolean) {
        currencyDao.upsertCurrency(currency.copy(isEnabled = isEnabled).toCurrencyEntity())
    }
}