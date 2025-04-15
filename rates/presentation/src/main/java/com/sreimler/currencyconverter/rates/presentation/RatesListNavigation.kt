package com.sreimler.currencyconverter.rates.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sreimler.currencyconverter.core.presentation.Screen

fun NavGraphBuilder.ratesNavigation() {
    composable(route = Screen.Rates.route) {
        RatesListScreenRoot()
    }
}