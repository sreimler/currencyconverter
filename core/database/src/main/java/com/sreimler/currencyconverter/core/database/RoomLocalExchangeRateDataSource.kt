package com.sreimler.currencyconverter.core.database

import com.sreimler.currencyconverter.core.database.dao.ExchangeRateDao
import com.sreimler.currencyconverter.core.database.mappers.toExchangeRate
import com.sreimler.currencyconverter.core.database.mappers.toExchangeRateEntity
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.LocalExchangeRateDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalExchangeRateDataSource(private val exchangeRateDao: ExchangeRateDao) : LocalExchangeRateDataSource {

    override suspend fun insertExchangeRate(exchangeRate: ExchangeRate) {
        exchangeRateDao.insertExchangeRate(exchangeRate.toExchangeRateEntity())
    }

    override suspend fun insertExchangeRates(exchangeRates: List<ExchangeRate>) {
        exchangeRateDao.insertExchangeRates(exchangeRates.map { it.toExchangeRateEntity() })
    }

    override fun getAllExchangeRates(): Flow<List<ExchangeRate>> {
        return exchangeRateDao.getExchangeRatesWithCurrencies().map { entities ->
            entities.map { it.toExchangeRate() }
        }
    }

    override fun getLatestExchangeRates(): Flow<List<ExchangeRate>> {
        return exchangeRateDao.getLatestExchangeRatesWithCurrencies().map { entities ->
            entities.map { it.toExchangeRate() }
        }
    }

    override fun getLatestExchangeRate(baseCurrency: Currency, targetCurrency: Currency): Flow<ExchangeRate> {
        return exchangeRateDao.getLatestExchangeRate(baseCurrency.symbol, targetCurrency.symbol)
            .map { it.toExchangeRate() }
    }

    override suspend fun deleteAllExchangeRates() {
        exchangeRateDao.deleteAllExchangeRates()
    }
}