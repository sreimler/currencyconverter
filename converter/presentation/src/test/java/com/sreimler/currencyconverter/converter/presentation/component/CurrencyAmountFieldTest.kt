package com.sreimler.currencyconverter.converter.presentation.component

import org.junit.Test
import kotlin.test.assertEquals

class CurrencyAmountFieldTest {

    @Test
    fun givenDoubleWithMoreThanFractionDigits_whenFormatted_isRoundedToFractionDigits() {
        val double = 2.843578
        val fractionDigits = 2
        val expected = "2.84"

        val actual = double.toFixedDecimalString(fractionDigits = fractionDigits)

        assertEquals(expected, actual)
    }

    @Test
    fun givenDoubleExactlyAtRoundingThreshold_whenFormatted_isRoundedUp() {
        val double = 2.005
        val fractionDigits = 2
        val expected = "2.01"

        val actual = double.toFixedDecimalString(fractionDigits = fractionDigits)

        assertEquals(expected, actual)
    }

    @Test
    fun givenDoubleWithLessThanFractionDigits_whenFormatted_hasCorrectNumberOfDigits() {
        val double = 2.0
        val fractionDigits = 5
        val expected = "2.00000"

        val actual = double.toFixedDecimalString(fractionDigits = fractionDigits)

        assertEquals(expected, actual)
    }
}
