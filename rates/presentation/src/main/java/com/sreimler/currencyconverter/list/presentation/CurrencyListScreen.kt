package com.sreimler.currencyconverter.list.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val decimalFormat = DecimalFormat("#,##0.0000")
private val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: CurrencyListViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::updateExchangeRates
    ) {
        CurrencyListScreen(state = viewModel.state.collectAsState().value, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListScreen(modifier: Modifier = Modifier, state: CurrencyListState) {

    Surface(modifier = modifier) {
        CurrencyList(
            state.exchangeRates.collectAsState(initial = listOf()),
            state.baseCurrency.collectAsState(initial = null),
            state.refreshDate.collectAsState(initial = null)
        )
    }
}

@Composable
fun CurrencyList(
    exchangeRates: State<List<ExchangeRate>>,
    baseCurrency: State<Currency?>,
    refreshDate: State<ZonedDateTime?>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val (base, list) = exchangeRates.value.partition { it.targetCurrency == baseCurrency.value }

        if (exchangeRates.value.isNotEmpty() && base.isNotEmpty() && refreshDate.value != null) {
            CurrencyCard(currency = base.first().targetCurrency, rate = base.first().rate)
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.weight(1f) // Ensures the grid takes up available space
            ) {
                items(
                    items = list.sortedBy { it.targetCurrency.name },
                    key = { exchangeRate -> exchangeRate.targetCurrency.code }) { exchangeRate ->
                    CurrencyCard(currency = exchangeRate.targetCurrency, rate = exchangeRate.rate)
                }
            }

            Text(
                text = "Last update: ${dateFormatter.format(refreshDate.value)}",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun CurrencyCard(currency: Currency, rate: Double) {
    Row {
        Text(text = "${currency.name} (${currency.symbol})", modifier = Modifier.weight(1f))
        Text(
            text = decimalFormat.format(rate),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun ListScreenPreview() {
    CurrencyConverterTheme {
        CurrencyListScreenRoot()
    }
}