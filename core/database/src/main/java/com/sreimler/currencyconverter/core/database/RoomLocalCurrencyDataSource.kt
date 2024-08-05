package com.sreimler.currencyconverter.core.database

import com.sreimler.currencyconverter.core.database.dao.CurrencyDao
import com.sreimler.currencyconverter.core.database.mappers.toCurrency
import com.sreimler.currencyconverter.core.database.mappers.toCurrencyEntity
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.LocalCurrencyDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override fun getCurrency(symbol: String): Flow<Currency> {
        return currencyDao.getCurrency(symbol).map { it.toCurrency() }
    }

    override suspend fun deleteCurrency(symbol: String) {
        currencyDao.deleteCurrency(symbol)
    }

    override suspend fun deleteAllCurrencies() {
        currencyDao.deleteAllCurrencies()
    }
}