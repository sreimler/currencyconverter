package com.sreimler.currencyconverter.converter.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ConverterViewModel : ViewModel() {

    private val _state by mutableStateOf(ConverterState())
}