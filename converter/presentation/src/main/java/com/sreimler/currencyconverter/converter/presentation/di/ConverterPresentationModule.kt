package com.sreimler.currencyconverter.converter.presentation.di

import com.sreimler.currencyconverter.converter.presentation.ConverterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val converterPresentationModule = module {
    viewModel { ConverterViewModel(get(), get()) }
}