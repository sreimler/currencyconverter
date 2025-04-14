package com.sreimler.currencyconverter.core.presentation.util

import android.icu.math.MathContext.ROUND_HALF_UP
import android.icu.text.DecimalFormat
import java.math.BigDecimal

private const val DEFAULT_MIN_SIGNIFICANT_DIGITS = 4

/**
 * Formats a double value to a string with a specified number of decimal places and significant digits.
 *
 * @param minDecimalPlaces The minimum number of decimal places to display.
 * @param minSignificantDigits The minimum number of significant digits to display.
 * @return A formatted string representation of the double value.
 */
fun Double.toFormattedUiString(
    minDecimalPlaces: Int,
    minSignificantDigits: Int = DEFAULT_MIN_SIGNIFICANT_DIGITS,
): String {
    // Check how many decimal places we need (which is based on minDecimalPlaces, minSignificantDigits and the double value)
    val intDigits = BigDecimal(this.toString()).toPlainString().split(".").first().length
    val fractionDigits = maxOf(minDecimalPlaces, minSignificantDigits - intDigits)

    val formatter = DecimalFormat().apply {
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
        minimumIntegerDigits = 1
        isGroupingUsed = true
        roundingMode = ROUND_HALF_UP
    }

    return formatter.format(this)
}
