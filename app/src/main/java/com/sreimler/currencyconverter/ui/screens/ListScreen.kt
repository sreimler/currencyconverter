package com.sreimler.currencyconverter.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sreimler.currencyconverter.data.EXCHANGE_RATE_LIST
import com.sreimler.currencyconverter.data.model.Currency
import com.sreimler.currencyconverter.data.model.ExchangeRate
import com.sreimler.currencyconverter.ui.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.viewmodel.CurrencyUiState
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val decimalFormat = DecimalFormat("#,##0.0000")
private val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

@Composable
fun ListScreen(modifier: Modifier = Modifier, uiState: CurrencyUiState) {
    Surface(modifier = modifier) {
        when (uiState) {
            is CurrencyUiState.Loading -> {}
            is CurrencyUiState.Error -> {}
            is CurrencyUiState.Success -> CurrencyList(
                uiState.exchangeRates, uiState.sourceCurrency, uiState
                    .refreshDate
            )
        }
    }
}

@Composable
fun CurrencyList(exchangeRates: List<ExchangeRate>, sourceCurrency: Currency, refreshDate: LocalDateTime) {
    Column {
        val (base, list) = exchangeRates.partition { it.currency == sourceCurrency }
        CurrencyCard(currency = base.first().currency, rate = base.first().rate)
        LazyVerticalGrid(columns = GridCells.Fixed(1)) {
            items(items = list, key = { exchangeRate -> exchangeRate.currency.code }) { exchangeRate ->
                CurrencyCard(currency = exchangeRate.currency, rate = exchangeRate.rate)
            }
        }
        Text(
            text = "Last update: ${dateFormatter.format(refreshDate)}",
            textAlign = TextAlign.End,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }

}

@Composable
fun CurrencyCard(currency: Currency, rate: Double) {
    Row {
        Text(text = "${currency.name} (${currency.symbol})", modifier = Modifier.weight(1f))
        Text(text = decimalFormat.format(rate), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Preview
@Composable
fun ListScreenPreview() {
    CurrencyConverterTheme {
        ListScreen(
            uiState = CurrencyUiState.Success(
                exchangeRates = EXCHANGE_RATE_LIST
            )
        )
    }
}