package com.sreimler.currencyconverter.converter.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sreimler.currencyconverter.converter.presentation.ConverterScreenRoot
import com.sreimler.currencyconverter.core.presentation.Screen

fun NavGraphBuilder.converterNavigation() {
    composable(route = Screen.Converter.route) {
        ConverterScreenRoot()
    }
}