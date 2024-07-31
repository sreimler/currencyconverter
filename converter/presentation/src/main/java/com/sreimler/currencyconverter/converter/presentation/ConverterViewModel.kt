package com.sreimler.currencyconverter.converter.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


@OptIn(ExperimentalFoundationApi::class)
class ConverterViewModel : ViewModel() {

    // TODO: check best approach for exposing state
    var state by mutableStateOf(ConverterState())
        private set
}