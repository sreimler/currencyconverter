package com.sreimler.currencyconverter.converter.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sreimler.currencyconverter.converter.presentation.component.AmountField
import com.sreimler.currencyconverter.converter.presentation.component.CurrencyAmountField
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_EUR
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_LIST
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_USD
import com.sreimler.currencyconverter.core.presentation.models.CurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.core.presentation.theme.StyledProgressIndicator
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
            modifier = modifier
        )
    }
}

@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    state: ConverterState,
    onAmountChanged: (AmountField, String) -> Unit,
    onCurrencySelected: (AmountField, CurrencyUi) -> Unit
) {
    Surface(modifier = modifier) {
        Column {
            state.sourceCurrency?.let {
                CurrencyRow(
                    field = AmountField.SOURCE,
                    amount = state.sourceAmount,
                    currency = it,
                    currencyList = state.currencyList,
                    onAmountChanged = onAmountChanged,
                    onCurrencySelected = onCurrencySelected
                )
            }

            state.targetCurrency?.let {
                CurrencyRow(
                    field = AmountField.TARGET,
                    amount = state.targetAmount,
                    currency = it,
                    currencyList = state.currencyList,
                    onAmountChanged = onAmountChanged,
                    onCurrencySelected = onCurrencySelected
                )
            }
        }
    }
}

@Composable
fun CurrencyRow(
    field: AmountField,
    amount: Double,
    currency: CurrencyUi,
    currencyList: List<CurrencyUi>,
    onAmountChanged: (AmountField, String) -> Unit,
    onCurrencySelected: (AmountField, CurrencyUi) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline, // or any color you like
                shape = RoundedCornerShape(12.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = currency.flagRes),
                contentDescription = currency.name,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currency.code,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            CurrencyDropdown(
                expanded = expanded,
                currencyList = currencyList,
                onDismiss = { expanded = false },
                onSelect = {
                    expanded = false
                    onCurrencySelected(field, it)
                }
            )
        }

        CurrencyAmountField(
            amount = amount,
            onAmountChanged = onAmountChanged,
            field = field
        )
    }
}

@Composable
fun CurrencyDropdown(
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
                onCurrencySelected = { _, _ -> }
            )
        }
    }
}