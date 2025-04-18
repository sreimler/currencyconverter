package com.sreimler.currencyconverter.core.data.di

import com.sreimler.currencyconverter.core.data.DataStoreCurrencyStorage
import com.sreimler.currencyconverter.core.data.OfflineFirstCurrencyRepository
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

// Provides instances of repository and preferences datastore
val dataModule = module {
    singleOf(::OfflineFirstCurrencyRepository).bind<CurrencyRepository>()
    single<LocalPreferredCurrencyStorage> { DataStoreCurrencyStorage(get()) }
}