package com.sreimler.currencyconverter.converter.presentation.component

import android.icu.text.DecimalFormatSymbols
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale

enum class AmountField {
    SOURCE, TARGET
}

/**
 * A composable input field for currency amounts.
 *
 * - Supports locale-specific decimal separators (e.g. '.' or ',')
 * - Restricts input to a single decimal point and valid digits
 * - Automatically selects all text when the field gains focus
 * - Syncs the displayed value with an external `Double` amount, avoiding redundant updates
 *
 * @param field The type of field (SOURCE or TARGET) to identify the input.
 * @param amount The current amount to display, synced with external state.
 * @param onAmountChanged Callback triggered when the user modifies the input.
 * @param modifier Modifier to customize the layout or behavior of the field.
 */
@Composable
fun CurrencyAmountField(
    field: AmountField,
    amount: Double,
    onAmountChanged: (AmountField, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Locale-specific decimal separator for user input
    val locale = remember { Locale.getDefault() }
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    val internalSeparator = '.' // Normalized separator for internal parsing

    // Track focus state and manage text selection behavior
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }

    // Manage the raw text input and ensure it matches the external amount
    var rawText by remember {
        mutableStateOf(if (amount == 0.0) "" else amount.toFixedDecimalString())
    }

    // TextFieldValue includes selection and cursor (TextRange(start==end))
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(rawText))
    }

    // Handles the full selection of text when the field gains focus
    var selectAllNext by remember { mutableStateOf(false) }

    // When focus is gained, schedule full selection
    LaunchedEffect(isFocused) {
        if (isFocused) {
            selectAllNext = true
        }
    }

    // Updates the displayed text when the external amount changes and the field is not focused
    LaunchedEffect(amount, isFocused) {
        if (!isFocused) {
            val formatted = amount.toFixedDecimalString()
            if (formatted != rawText) {
                rawText = formatted
                textFieldValue = TextFieldValue(formatted)
            }
        }
    }

    // TextField for user input with validation and formatting
    TextField(
        value = if (selectAllNext) {
            textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
        } else {
            textFieldValue
        },
        onValueChange = { newValue ->
            // Normalize input to internal '.' for decimal separator
            val normalized = newValue.text.replace(decimalSeparator, internalSeparator).replace(',', '.')

            // Accept only valid numeric input with max 1 decimal point and up to 4 decimals
            val isValid = normalized.matches(Regex("""\d*([.]?\d{0,4})?"""))

            if (isValid) {
                textFieldValue = newValue
                rawText = newValue.text
                selectAllNext = false

                val parsed = normalized.toDoubleOrNull()
                if (parsed != null) {
                    // Prevent unnecessary updates by comparing fixed-format strings
                    if (parsed.toFixedDecimalString() != amount.toFixedDecimalString()) {
                        onAmountChanged(field, normalized)
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            errorContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            errorIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
        ),
        textStyle = androidx.compose.ui.text.TextStyle.Default.copy(
            textAlign = TextAlign.End,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

/**
 * Formats a double to a fixed number of decimal places using HALF_UP rounding.
 * Used for both input display and equality checks.
 */
fun Double.toFixedDecimalString(fractionDigits: Int = 2): String {
    return DecimalFormat("0.${"0".repeat(fractionDigits)}").apply {
        roundingMode = RoundingMode.HALF_UP
    }.format(this)
}
