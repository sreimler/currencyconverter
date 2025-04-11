package com.sreimler.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.sreimler.currencyconverter.core.presentation.CurrencyConversionApp
import com.sreimler.currencyconverter.navigation.MainNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            CurrencyConversionApp(navController) {
                MainNavHost(navController = navController)
            }
        }
    }
}
