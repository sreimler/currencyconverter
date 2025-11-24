@file:OptIn(ExperimentalAnimationApi::class)

package com.sreimler.currencyconverter.rates.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_USD
import com.sreimler.currencyconverter.core.domain.mock.ExchangeRateMock.EXCHANGE_RATES
import com.sreimler.currencyconverter.core.presentation.component.CurrencyFlagImage
import com.sreimler.currencyconverter.core.presentation.component.StyledCurrencyRow
import com.sreimler.currencyconverter.core.presentation.component.StyledProgressIndicator
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.core.presentation.util.toFormattedUiString
import com.sreimler.currencyconverter.core.presentation.util.toString
import org.koin.androidx.compose.koinViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesListScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: RatesListViewModel = koinViewModel(),
    onNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Display viewmodel errors as toasts
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.errors.collect { error ->
            Toast.makeText(context, error.toString(context), Toast.LENGTH_SHORT).show()
        }
    }

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
            },
            onItemLongClicked = { currencyUi ->
                viewModel.onCurrencyLongClicked(currencyUi)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesListScreen(
    modifier: Modifier = Modifier,
    state: RatesListState,
    onItemClicked: (CurrencyUi) -> Unit,
    onItemLongClicked: (CurrencyUi) -> Unit
) {
    Surface(modifier = modifier) {
        RatesList(
            exchangeRates = state.exchangeRates,
            baseCurrency = state.baseCurrency,
            refreshDate = state.refreshDate,
            onItemClicked = onItemClicked,
            onItemLongClicked = onItemLongClicked
        )
    }
}

@Composable
fun RatesList(
    exchangeRates: List<ExchangeRateUi>,
    baseCurrency: CurrencyUi?,
    refreshDate: ZonedDateTime?,
    onItemClicked: (CurrencyUi) -> Unit,
    onItemLongClicked: (CurrencyUi) -> Unit
) {
    // Smoothen the transition when changing the base currency
    AnimatedContent(
        targetState = baseCurrency,
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 1500)) togetherWith fadeOut()
        }
    ) { currentBaseCurrency ->
        Column(modifier = Modifier.fillMaxSize()) {
            val (base, list) = exchangeRates.partition { it.targetCurrency == currentBaseCurrency }

            if (exchangeRates.isNotEmpty() && base.isNotEmpty() && refreshDate != null) {
                RatesListItem(
                    currency = base.first().targetCurrency,
                    exchangeRate = base.first(),
                    onItemClicked = onItemClicked,
                    onItemLongClicked = { } // This already is the base currency, no need to change
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.weight(1f) // Ensures the grid takes up available space
                ) {
                    items(
                        items = list.sortedBy { it.targetCurrency.name },
                        key = { exchangeRate -> exchangeRate.targetCurrency.code }) { exchangeRate ->
                        RatesListItem(
                            currency = exchangeRate.targetCurrency,
                            exchangeRate = exchangeRate,
                            onItemClicked = onItemClicked,
                            onItemLongClicked = onItemLongClicked
                        )
                    }
                }

                Text(
                    text = "Last update: ${dateFormatter.format(refreshDate)}",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun RatesListItem(
    currency: CurrencyUi,
    exchangeRate: ExchangeRateUi,
    onItemClicked: (CurrencyUi) -> Unit,
    onItemLongClicked: (CurrencyUi) -> Unit
) {
    StyledCurrencyRow(onClick = { onItemClicked(currency) }, onLongClick = { onItemLongClicked(currency) }) {
        CurrencyFlagImage(currency.flagRes, currency.name)
        CurrencyName(currency.name)
        CurrencyExchangeRate(currency, exchangeRate)
    }
}

@Composable
private fun CurrencyName(currencyName: String) {
    Text(
        text = currencyName,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
private fun CurrencyExchangeRate(currency: CurrencyUi, exchangeRate: ExchangeRateUi) {
    Text(
        text = "${currency.symbolNative} ${exchangeRate.rate.toFormattedUiString(currency.decimalDigits)}",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth()
    )
}

@PreviewLightDark
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
                    exchangeRates = EXCHANGE_RATES.map { it.toExchangeRateUi() },
                    baseCurrency = CURRENCY_USD.toCurrencyUi(),
                    refreshDate = ZonedDateTime.now(),
                    onItemClicked = { },
                    onItemLongClicked = { }
                )
            }
        }
    }
}
