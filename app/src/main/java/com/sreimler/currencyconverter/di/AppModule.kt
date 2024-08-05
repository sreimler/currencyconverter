package com.sreimler.currencyconverter.di

import com.sreimler.currencyconverter.core.data.networking.NetworkCurrencyRepository
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import org.koin.dsl.module


val appModule = module {
    //single<CurrencyRepository> { LocalCurrencyRepository() }
    single<CurrencyRepository> { NetworkCurrencyRepository() }
}