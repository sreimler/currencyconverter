package com.sreimler.currencyconverter.converter.presentation.component

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class CurrencyAmountFieldTest {

    @get:Rule
    val composeAndroidTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shouldSelectAllText_whenNonFocusedFieldIsClicked() {
        composeAndroidTestRule.setContent {
            CurrencyAmountField(
                AmountField.SOURCE,
                1.00,
                { _, _ -> }
            )
        }

        with(composeAndroidTestRule) {
            onNodeWithText("1.00").performClick()
            onNodeWithText("1.00").assertIsFocused()
        }
    }
}
