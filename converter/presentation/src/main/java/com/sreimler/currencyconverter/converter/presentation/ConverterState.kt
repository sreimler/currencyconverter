package com.sreimler.currencyconverter.converter.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
//import androidx.compose.foundation.text2.input.TextFieldState
import com.sreimler.currencyconverter.core.domain.Currency

@OptIn(ExperimentalFoundationApi::class)
data class ConverterState(
    val sourceCurrency: Currency? = null,
    val sourceAmount: TextFieldState = TextFieldState(),
    val targetCurrency: Currency? = null,
    val targetAmount: TextFieldState = TextFieldState()
)
