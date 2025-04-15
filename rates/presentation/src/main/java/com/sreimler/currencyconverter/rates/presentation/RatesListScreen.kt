package com.sreimler.currencyconverter.rates.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_USD
import com.sreimler.currencyconverter.core.domain.mock.ExchangeRateMock.EXCHANGE_RATES
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.core.presentation.util.toFormattedUiString
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesListScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: RatesViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::updateExchangeRates
    ) {
        RatesListScreen(state = viewModel.state.collectAsState().value, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesListScreen(modifier: Modifier = Modifier, state: RatesListState) {
    Surface(modifier = modifier) {
        RatesList(
            state.exchangeRates.collectAsState(initial = listOf()),
            state.baseCurrency.collectAsState(initial = null),
            state.refreshDate.collectAsState(initial = null)
        )
    }
}

@Composable
fun RatesList(
    exchangeRates: State<List<ExchangeRateUi>>,
    baseCurrency: State<CurrencyUi?>,
    refreshDate: State<ZonedDateTime?>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val (base, list) = exchangeRates.value.partition { it.targetCurrency == baseCurrency.value }

        if (exchangeRates.value.isNotEmpty() && base.isNotEmpty() && refreshDate.value != null) {
            CurrencyCard(currency = base.first().targetCurrency, exchangeRate = base.first())
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.weight(1f) // Ensures the grid takes up available space
            ) {
                items(
                    items = list.sortedBy { it.targetCurrency.name },
                    key = { exchangeRate -> exchangeRate.targetCurrency.code }) { exchangeRate ->
                    CurrencyCard(currency = exchangeRate.targetCurrency, exchangeRate = exchangeRate)
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
fun CurrencyCard(currency: CurrencyUi, exchangeRate: ExchangeRateUi) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = currency.flagRes),
            contentDescription = currency.name
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = currency.name, modifier = Modifier.weight(1f))
        Timber.d(
            "Currency: ${currency.name}, Decimal Digits: ${currency.decimalDigits} Rate: ${exchangeRate.rate}, Formatted: ${
                exchangeRate.rate.toFormattedUiString(
                    currency.decimalDigits
                )
            }"
        )
        Text(
            text = "${currency.symbolNative} ${exchangeRate.rate.toFormattedUiString(currency.decimalDigits)}",
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun ListScreenPreview() {
    CurrencyConverterTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp) // Padding applied only to content
            ) {
                RatesList(
                    exchangeRates = MutableStateFlow(EXCHANGE_RATES.map { it.toExchangeRateUi() }).collectAsState(),
                    baseCurrency = MutableStateFlow(CURRENCY_USD.toCurrencyUi()).collectAsState(),
                    refreshDate = MutableStateFlow(ZonedDateTime.now()).collectAsState()
                )
            }
        }
    }
}