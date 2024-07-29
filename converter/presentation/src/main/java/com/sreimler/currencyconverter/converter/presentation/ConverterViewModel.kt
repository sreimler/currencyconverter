package com.sreimler.currencyconverter.converter.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

@Suppress("OPT_IN_USAGE_FUTURE_ERROR")
class ConverterViewModel : ViewModel() {

    // TODO: check best approach for exposing state
    var state by mutableStateOf(ConverterState())
        private set
}