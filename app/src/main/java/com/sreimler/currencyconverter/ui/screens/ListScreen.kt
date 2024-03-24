package com.sreimler.currencyconverter.ui.screens

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sreimler.currencyconverter.data.model.Currency
import com.sreimler.currencyconverter.data.model.ExchangeRate
import com.sreimler.currencyconverter.viewmodel.CurrencyUiState


@Composable
fun ListScreen(modifier: Modifier = Modifier, uiState: CurrencyUiState) {
    Surface(modifier = modifier) {
        when (uiState) {
            is CurrencyUiState.Loading -> {}
            is CurrencyUiState.Error -> {}
            is CurrencyUiState.Success -> CurrencyList(uiState.exchangeRates)
        }
    }
}

@Composable
fun CurrencyList(exchangeRates: List<ExchangeRate>) {
    LazyVerticalGrid(columns = GridCells.Fixed(1)) {
        items(items = exchangeRates, key = { exchangeRate -> exchangeRate.currency.code }) { exchangeRate ->
            CurrencyCard(currency = exchangeRate.currency, rate = exchangeRate.rate)
        }
    }
}

@Composable
fun CurrencyCard(currency: Currency, rate: Double) {
    Text("${currency.name} (${currency.symbol}) $rate")
}