package com.sreimler.currencyconverter.ui.screens

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.sreimler.currencyconverter.data.model.Currency
import com.sreimler.currencyconverter.viewmodel.CurrencyUiState


@Composable
fun ListScreen(modifier: Modifier = Modifier, uiState: State<CurrencyUiState>) {
    Surface(modifier = modifier) {
        when (val state = uiState.value) {
            is CurrencyUiState.Loading -> {}
            is CurrencyUiState.Error -> {}
            is CurrencyUiState.Success -> CurrencyList(state.currencies)
        }
    }
}

@Composable
fun CurrencyList(currencies: List<Currency>) {
    LazyVerticalGrid(columns = GridCells.Fixed(1)) {
        items(items = currencies, key = { currency -> currency.symbol }) {
            CurrencyCard(currency = it)
        }
    }
}

@Composable
fun CurrencyCard(currency: Currency) {
    Text("${currency.name} (${currency.symbol})")
}