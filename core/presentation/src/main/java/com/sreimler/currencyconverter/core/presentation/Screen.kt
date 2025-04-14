package com.sreimler.currencyconverter.core.presentation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object Rates : Screen("rates", R.string.rates, Icons.AutoMirrored.Filled.ShowChart)
    object Converter : Screen("converter", R.string.convert, Icons.AutoMirrored.Filled.CompareArrows)
    object Settings : Screen("settings", R.string.settings, Icons.Filled.Settings)

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