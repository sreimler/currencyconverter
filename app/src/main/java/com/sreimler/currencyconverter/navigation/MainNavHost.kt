package com.sreimler.currencyconverter.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sreimler.currencyconverter.converter.presentation.navigation.converterNavigation
import com.sreimler.currencyconverter.core.presentation.Screen
import com.sreimler.currencyconverter.presentation.InfoScreen
import com.sreimler.currencyconverter.rates.presentation.navigation.ratesNavigation

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Rates.route,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ratesNavigation(onNavigate = { navController.navigate(Screen.Converter.route) })
        converterNavigation()

        // TODO: refactor into settings module
//        settingsNavigation()
        composable(route = Screen.Settings.route) {
            InfoScreen()
        }
    }
}