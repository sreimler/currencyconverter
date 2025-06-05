package com.sreimler.currencyconverter.converter.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sreimler.currencyconverter.converter.presentation.component.AmountField
import com.sreimler.currencyconverter.converter.presentation.component.CurrencyAmountField
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_EUR
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_LIST
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_USD
import com.sreimler.currencyconverter.core.presentation.component.CurrencyFlagImage
import com.sreimler.currencyconverter.core.presentation.component.StyledCurrencyRow
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.core.presentation.theme.StyledProgressIndicator
import com.sreimler.currencyconverter.core.presentation.util.toFormattedUiString
import org.koin.androidx.compose.koinViewModel


@Composable
fun ConverterScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: ConverterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Triggers once every time this screen is shown
    LaunchedEffect(Unit) {
        viewModel.refreshConversionState()
    }

    if (state.isLoading) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            StyledProgressIndicator()
        }
    } else {
        ConverterScreen(
            state = state,
            onAmountChanged = viewModel::onAmountChanged,
            onCurrencySelected = viewModel::onCurrencySelected,
            onSwapCurrencies = viewModel::onSwapCurrencies,
            modifier = modifier
        )
    }
}

@Composable
fun ConverterScreen(
    state: ConverterState,
    onAmountChanged: (AmountField, String) -> Unit,
    onCurrencySelected: (AmountField, CurrencyUi) -> Unit,
    onSwapCurrencies: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Box is used to position the FAB centered, overlaying the two currency rows
        Box(
            modifier = modifier.wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.wrapContentHeight()
            ) {
                state.sourceCurrency?.let {
                    CurrencyRow(
                        field = AmountField.SOURCE,
                        amount = state.sourceAmount,
                        currency = state.sourceCurrency,
                        currencyList = state.currencyList,
                        onAmountChanged = onAmountChanged,
                        onCurrencySelected = onCurrencySelected
                    )
                }

                state.targetCurrency?.let {
                    CurrencyRow(
                        field = AmountField.TARGET,
                        amount = state.targetAmount,
                        currency = state.targetCurrency,
                        currencyList = state.currencyList,
                        onAmountChanged = onAmountChanged,
                        onCurrencySelected = onCurrencySelected
                    )
                }
            }

            // FAB inside the Box
            SwapCurrencyFab(onClick = onSwapCurrencies)
        }

        // Conversion rate text below the Box
        ConversionRateText(state = state)
    }
}


@Composable
fun CurrencyRow(
    field: AmountField,
    amount: Double,
    currency: CurrencyUi,
    currencyList: List<CurrencyUi>,
    onAmountChanged: (AmountField, String) -> Unit,
    onCurrencySelected: (AmountField, CurrencyUi) -> Unit,
    modifier: Modifier = Modifier
) {
    StyledCurrencyRow(modifier = modifier) {
        CurrencyDropdownElement(
            currency = currency,
            currencyList = currencyList,
            onCurrencySelected = onCurrencySelected,
            field = field
        )

        CurrencyAmountField(
            amount = amount,
            onAmountChanged = onAmountChanged,
            field = field
        )
    }
}

@Composable
fun CurrencyDropdownElement(
    currency: CurrencyUi,
    currencyList: List<CurrencyUi>,
    onCurrencySelected: (AmountField, CurrencyUi) -> Unit,
    field: AmountField,
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .clickable {
                focusManager.clearFocus()
                expanded = !expanded
            }
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyFlagImage(
            flagRes = currency.flagRes,
            contentDescription = currency.name
        )
        Text(
            text = currency.code,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        DropdownList(
            expanded = expanded,
            currencyList = currencyList,
            onDismiss = { expanded = false },
            onSelect = {
                expanded = false
                onCurrencySelected(field, it)
            }
        )
    }
}

@Composable
fun DropdownList(
    expanded: Boolean,
    currencyList: List<CurrencyUi>,
    onDismiss: () -> Unit,
    onSelect: (CurrencyUi) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        currencyList.forEach { currency ->
            DropdownMenuItem(
                leadingIcon = {
                    Image(
                        painter = painterResource(id = currency.flagRes),
                        contentDescription = currency.name
                    )
                },
                text = { Text(currency.code) },
                onClick = { onSelect(currency) }
            )
        }
    }
}

@Composable
fun ConversionRateText(modifier: Modifier = Modifier, state: ConverterState) {
    Text(
        text = stringResource(
            id = R.string.exchange_rate,
            state.sourceCurrency?.code ?: "",
            state.exchangeRate.toFormattedUiString(minDecimalPlaces = 4),
            state.targetCurrency?.code ?: ""
        ),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(top = 16.dp)
    )
}

@Composable
fun SwapCurrencyFab(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val focusManager = LocalFocusManager.current

    FloatingActionButton(
        onClick = {
            // Clear focus of CurrencyAmountFields to allow update of values when swapping
            focusManager.clearFocus()
            onClick()
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        shape = CircleShape,
        modifier = modifier.zIndex(1f)
    ) {
        Icon(Icons.Default.SwapVert, contentDescription = stringResource(R.string.swap_currencies))
    }
}

@Preview
@Composable
fun ConverterScreenPreview() {
    CurrencyConverterTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ConverterScreen(
                state = ConverterState(
                    currencyList = CURRENCY_LIST.map { it.toCurrencyUi() },
                    sourceCurrency = CURRENCY_USD.toCurrencyUi(),
                    sourceAmount = 123.53,
                    targetCurrency = CURRENCY_EUR.toCurrencyUi(),
                    targetAmount = 210.01
                ),
                onAmountChanged = { _, _ -> },
                onCurrencySelected = { _, _ -> },
                onSwapCurrencies = {}
            )
        }
    }
}