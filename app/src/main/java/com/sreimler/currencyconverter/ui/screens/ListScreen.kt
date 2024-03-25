package com.sreimler.currencyconverter.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sreimler.currencyconverter.data.EXCHANGE_RATE_LIST
import com.sreimler.currencyconverter.data.model.Currency
import com.sreimler.currencyconverter.data.model.ExchangeRate
import com.sreimler.currencyconverter.ui.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.viewmodel.CurrencyUiState
import java.text.DecimalFormat

private val df = DecimalFormat("#,##0.0000")

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
    Row {
        Text(text = "${currency.name} (${currency.symbol})", modifier = Modifier.weight(1f))
        Text(text = df.format(rate), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Preview
@Composable
fun ListScreenPreview() {
    CurrencyConverterTheme {
        ListScreen(uiState = CurrencyUiState.Success(exchangeRates = EXCHANGE_RATE_LIST))
    }
}