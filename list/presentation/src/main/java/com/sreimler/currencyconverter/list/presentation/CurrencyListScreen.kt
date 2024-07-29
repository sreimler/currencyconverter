package com.sreimler.currencyconverter.list.presentation

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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.viewmodel.CurrencyListViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val decimalFormat = DecimalFormat("#,##0.0000")
private val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

@Composable
fun CurrencyListScreenRoot(modifier: Modifier = Modifier, viewModel: CurrencyListViewModel = koinViewModel()) {
    CurrencyListScreen(state = viewModel.state.collectAsState())
}

@Composable
fun CurrencyListScreen(modifier: Modifier = Modifier, state: State<CurrencyListState>) {
    Surface(modifier = modifier) {
        val currenctState = state.value

        when (currenctState) {
            is CurrencyListState.Loading -> {}
            is CurrencyListState.Error -> {}
            is CurrencyListState.Success -> CurrencyList(
                currenctState.exchangeRates,
                currenctState.sourceCurrency,
                currenctState.refreshDate
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
        CurrencyListScreenRoot()
    }
}