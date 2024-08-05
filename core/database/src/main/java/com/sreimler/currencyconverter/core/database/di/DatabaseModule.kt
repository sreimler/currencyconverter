package com.sreimler.currencyconverter.core.database.di

import androidx.room.Room
import com.sreimler.currencyconverter.core.database.CurrencyDatabase
import com.sreimler.currencyconverter.core.database.RoomLocalCurrencyDataSource
import com.sreimler.currencyconverter.core.database.RoomLocalExchangeRateDataSource
import com.sreimler.currencyconverter.core.domain.LocalCurrencyDataSource
import com.sreimler.currencyconverter.core.domain.LocalExchangeRateDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            CurrencyDatabase::class.java,
            "currency.db"
        ).build()
    }

    single { get<CurrencyDatabase>().currencyDao }
    single { get<CurrencyDatabase>().exchangeRateDao }

    singleOf(::RoomLocalCurrencyDataSource).bind<LocalCurrencyDataSource>()
    singleOf(::RoomLocalExchangeRateDataSource).bind<LocalExchangeRateDataSource>()
}