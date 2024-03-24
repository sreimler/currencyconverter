package com.sreimler.currencyconverter.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sreimler.currencyconverter.data.CURRENCY_EUR
import com.sreimler.currencyconverter.data.CURRENCY_USD
import com.sreimler.currencyconverter.data.EXCHANGE_RATE_LIST
import com.sreimler.currencyconverter.data.model.Currency
import com.sreimler.currencyconverter.viewmodel.CurrencyUiState
import java.text.DecimalFormat

private val df = DecimalFormat("#,##0.00")

@Composable
fun ConverterScreen(modifier: Modifier = Modifier, uiState: CurrencyUiState, onChange: (String, Currency) -> Unit) {
    Surface(modifier = modifier) {
        if (uiState is CurrencyUiState.Success) {
            Column {
                CurrencyRow(currency = uiState.sourceCurrency, amount = uiState.sourceAmount, onChange = onChange)
                CurrencyRow(currency = uiState.targetCurrency, amount = uiState.targetAmount, onChange = onChange)
            }
        }
    }
}

@Composable
fun CurrencyRow(currency: Currency, amount: Double, onChange: (String, Currency) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = currency.name,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )

        // This does not allow to set the text cursor
        // TODO: fix!
        //var textFieldValue by remember(key1 = amount) { mutableStateOf(TextFieldValue(df.format(amount))) }
        //val interactionSource = remember { MutableInteractionSource() }
        //val isFocused by interactionSource.collectIsFocusedAsState()
        //
        //// Select whole input text when gaining focus
        //LaunchedEffect(isFocused) {
        //    val endRange = if (isFocused) textFieldValue.text.length else 0
        //
        //    textFieldValue = textFieldValue.copy(
        //        selection = TextRange(
        //            start = 0,
        //            end = endRange
        //        )
        //    )
        //}

        TextField(
            value = df.format(amount),
            onValueChange = { onChange(it, currency) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            //interactionSource = interactionSource,
            textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.End),
            label = { Text(currency.code) }
        )

    }
}

@Preview
@Composable
fun ConverterScreenPreview() {
    ConverterScreen(
        uiState = CurrencyUiState.Success(
            exchangeRates = EXCHANGE_RATE_LIST,
            sourceCurrency = CURRENCY_USD,
            targetCurrency = CURRENCY_EUR,
            sourceAmount = 5.00,
            targetAmount = 6.33
        ),
        onChange = { _, _ -> }
    )
}