package com.sreimler.currencyconverter.converter.presentation.component

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.sreimler.currencyconverter.core.presentation.CurrencyConversionScaffold
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme
import org.junit.Rule
import org.junit.Test

class CurrencyAmountFieldTest {

    @get:Rule
//    val composeTestRule = createComposeRule()
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun givenFieldNotFocus_whenClicked_thenSelectsAllText() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CurrencyConverterTheme {
                CurrencyConversionScaffold(navController) {
                    CurrencyAmountField(
                        AmountField.SOURCE,
                        1.00,
                        { _, _ -> }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("1.00").performClick()
        composeTestRule.onNodeWithText("1.00").assertIsFocused()
    }
}
