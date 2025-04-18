package com.sreimler.currencyconverter.rates.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sreimler.currencyconverter.core.presentation.Screen
import com.sreimler.currencyconverter.rates.presentation.RatesListScreenRoot

fun NavGraphBuilder.ratesNavigation(
    onNavigate: () -> Unit
) {
    composable(route = Screen.Rates.route) {
        RatesListScreenRoot(onNavigate = onNavigate)
    }
}