package com.sreimler.currencyconverter.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sreimler.currencyconverter.core.database.dao.CurrencyDao
import com.sreimler.currencyconverter.core.database.dao.ExchangeRateDao
import com.sreimler.currencyconverter.core.database.entity.CurrencyEntity
import com.sreimler.currencyconverter.core.database.entity.ExchangeRateEntity

@Database(
    entities = [
        CurrencyEntity::class,
        ExchangeRateEntity::class
    ],
    version = 1
)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract val currencyDao: CurrencyDao
    abstract val exchangeRateDao: ExchangeRateDao
}