package com.sreimler.currencyconverter.core.presentation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object Rates : Screen("rates", R.string.rates, Icons.AutoMirrored.Filled.List)
    object Converter : Screen("converter", R.string.convert, Icons.Default.AttachMoney)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Info)

    companion object {
        val bottomNavItems = listOf(Rates, Converter, Settings)

        fun fromRoute(route: String?): Screen {
            return when (route) {
                Rates.route -> Rates
                Converter.route -> Converter
                Settings.route -> Settings
                else -> Rates // fallback if route is null or unrecognized
            }
        }
    }
}