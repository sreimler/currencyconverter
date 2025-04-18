package com.sreimler.currencyconverter.rates.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.sreimler.currencyconverter.core.presentation.theme.StyledProgressIndicator
import com.sreimler.currencyconverter.core.presentation.util.toFormattedUiString
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesListScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: RatesViewModel = koinViewModel(),
    onNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val refreshState = rememberPullToRefreshState()
    val isRefreshing = state.isRefreshing
    val onRefresh = viewModel::updateExchangeRates

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.pullToRefresh(
            isRefreshing = isRefreshing,
            state = refreshState,
            onRefresh = onRefresh
        ),
        indicator = {
            if (isRefreshing) {
                StyledProgressIndicator()
            }
        }
    ) {
        RatesListScreen(
            state = state,
            modifier = modifier,
            onItemClicked = { currencyUi ->
                viewModel.onCurrencyClicked(currencyUi)
                onNavigate()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesListScreen(modifier: Modifier = Modifier, state: RatesListState, onItemClicked: (CurrencyUi) -> Unit) {
    Surface(modifier = modifier) {
        RatesList(
            exchangeRates = state.exchangeRates.collectAsState(initial = listOf()),
            baseCurrency = state.baseCurrency.collectAsState(initial = null),
            refreshDate = state.refreshDate.collectAsState(initial = null),
            onItemClicked = onItemClicked
        )
    }
}

@Composable
fun RatesList(
    exchangeRates: State<List<ExchangeRateUi>>,
    baseCurrency: State<CurrencyUi?>,
    refreshDate: State<ZonedDateTime?>,
    onItemClicked: (CurrencyUi) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val (base, list) = exchangeRates.value.partition { it.targetCurrency == baseCurrency.value }

        if (exchangeRates.value.isNotEmpty() && base.isNotEmpty() && refreshDate.value != null) {
            CurrencyCard(
                currency = base.first().targetCurrency,
                exchangeRate = base.first(),
                onItemClicked = onItemClicked
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.weight(1f) // Ensures the grid takes up available space
            ) {
                items(
                    items = list.sortedBy { it.targetCurrency.name },
                    key = { exchangeRate -> exchangeRate.targetCurrency.code }) { exchangeRate ->
                    CurrencyCard(
                        currency = exchangeRate.targetCurrency,
                        exchangeRate = exchangeRate,
                        onItemClicked = onItemClicked
                    )
                }
            }

            Text(
                text = "Last update: ${dateFormatter.format(refreshDate.value)}",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun CurrencyCard(
    currency: CurrencyUi,
    exchangeRate: ExchangeRateUi,
    onItemClicked: (CurrencyUi) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .clickable { onItemClicked(currency) },
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = currency.flagRes),
                contentDescription = currency.name,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = currency.name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${currency.symbolNative} ${exchangeRate.rate.toFormattedUiString(currency.decimalDigits)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.End
            )
        }
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
                    refreshDate = MutableStateFlow(ZonedDateTime.now()).collectAsState(),
                    onItemClicked = { }
                )
            }
        }
    }
}