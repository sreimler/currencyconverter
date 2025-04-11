package com.sreimler.currencyconverter.converter.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sreimler.currencyconverter.core.domain.Currency
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat

private val df = DecimalFormat("#,##0.00")

@Composable
fun ConverterScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: ConverterViewModel = koinViewModel()
) {
    ConverterScreen(state = viewModel.state)
}

@Composable
fun ConverterScreen(modifier: Modifier = Modifier, state: ConverterState) {
    Surface(modifier = modifier) {
        if (state.sourceCurrency != null && state.targetCurrency != null) {
            Column {
                CurrencyRow(currency = state.sourceCurrency, textFieldState = state.sourceAmount)
                CurrencyRow(currency = state.targetCurrency, textFieldState = state.targetAmount)
            }
        }
    }
}

@Composable
fun CurrencyRow(currency: Currency, textFieldState: TextFieldState) {
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

//        BasicTextField2(
//            //value = df.format(amount),
//            //onValueChange = { viewModel.(it, currency) },
//            state = textFieldState,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Decimal
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            //interactionSource = interactionSource,
//            textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.End),
//            //label = { Text(currency.code) }
//        )

    }
}

@Preview
@Composable
fun ConverterScreenPreview() {
    ConverterScreen(state = ConverterState()) // TODO: emulate currencies
}