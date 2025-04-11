package com.sreimler.currencyconverter.list.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sreimler.currencyconverter.core.presentation.Screen

fun NavGraphBuilder.ratesNavigation() {
    composable(route = Screen.Rates.route) {
        CurrencyListScreenRoot()
    }
}