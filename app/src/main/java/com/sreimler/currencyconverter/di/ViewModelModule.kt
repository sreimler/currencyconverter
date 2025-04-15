package com.sreimler.currencyconverter.di

import com.sreimler.currencyconverter.converter.presentation.ConverterViewModel
import com.sreimler.currencyconverter.rates.presentation.RatesViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val viewModelModule = module {
    viewModelOf(::ConverterViewModel)
    viewModelOf(::RatesViewModel)
}