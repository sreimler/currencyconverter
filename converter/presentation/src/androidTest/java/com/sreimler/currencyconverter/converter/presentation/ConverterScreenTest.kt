package com.sreimler.currencyconverter.converter.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.sreimler.currencyconverter.converter.presentation.component.AmountField
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_EUR
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_GBP
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_LIST
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_USD
import com.sreimler.currencyconverter.core.domain.util.AppError
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.core.presentation.util.toFormattedUiString
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.junit.Rule
import org.junit.Test

class ConverterScreenTest {

    val sourceAmount = 123.53
    val targetAmount = 210.01
    val sourceCurrency = CURRENCY_USD.toCurrencyUi()
    val targetCurrency = CURRENCY_EUR.toCurrencyUi()
    val currencyList = CURRENCY_LIST.map { it.toCurrencyUi() }
    val defaultState = ConverterState(
        isLoading = false,
        currencyList = currencyList,
        sourceCurrency = sourceCurrency,
        sourceAmount = sourceAmount,
        targetCurrency = targetCurrency,
        targetAmount = targetAmount
    )
    val errorFlow: SharedFlow<AppError> = MutableSharedFlow()

    @get:Rule
    val composeAndroidTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setDefaultContent() {
        composeAndroidTestRule.setContent {
            CurrencyConverterTheme {
                ConverterScreenRoot(
                    state = defaultState,
                    errors = errorFlow,
                    onAmountChanged = { _, _ -> },
                    onCurrencySelected = { _, _ -> },
                    onSwapCurrencies = {}
                )
            }
        }
    }

    @Test
    fun shouldDisplayValuesFromState_whenScreenLoadsInitially() {
        setDefaultContent()

        with(composeAndroidTestRule) {
            // Check source and target currency values and codes exist
            onNodeWithText(sourceAmount.toString()).assertExists()
            onNodeWithText(targetAmount.toString()).assertExists()
            onNodeWithText(sourceCurrency.code).assertExists()
            onNodeWithText(targetCurrency.code).assertExists()

            // Ensure loading spinner not displayed
            onNodeWithTag(activity.getString(com.sreimler.currencyconverter.core.presentation.R.string.test_tag_styled_progress_indicator)).assertDoesNotExist()
        }
    }

    @Test
    fun shouldDisplayConversionRateString_whenScreenLoadsInitially() {
        setDefaultContent()

        with(composeAndroidTestRule) {
            val formattedExchangeRate = defaultState.exchangeRate.toFormattedUiString(minDecimalPlaces = 4)
            val conversionRateString = activity.getString(
                R.string.exchange_rate,
                sourceCurrency.code,
                formattedExchangeRate,
                targetCurrency.code
            )

            onNodeWithText(conversionRateString).assertExists()
        }
    }

    @Test
    fun shouldDisplayAllCurrenciesInSourceDropdown_whenClicked() {
        setDefaultContent()

        with(composeAndroidTestRule) {
            // Check all currency codes in source dropdown
            onNodeWithText(sourceCurrency.code).performClick()
            currencyList.forEach { currency ->
                onAllNodesWithText(currency.code)
                    .assertCountEquals(
                        if (currency.code == sourceCurrency.code ||
                            currency.code == targetCurrency.code
                        ) 2 else 1
                    )
            }
        }
    }

    @Test
    fun shouldDisplayAllCurrenciesInTargetDropdown_whenClicked() {
        setDefaultContent()

        with(composeAndroidTestRule) {
            // Check all currency codes in target dropdown
            onNodeWithText(targetCurrency.code).performClick()
            currencyList.forEach { currency ->
                onAllNodesWithText(currency.code)
                    .assertCountEquals(
                        if (currency.code == sourceCurrency.code ||
                            currency.code == targetCurrency.code
                        ) 2 else 1
                    )
            }
        }
    }

    @Test
    fun shouldInvokeCalculation_whenSourceAmountChanged() {
        var wasInvoked = false
        var invokedWithAmount: Double? = null
        var invokedWithIsSource: Boolean? = null
        val newSourceAmountString = "100"

        composeAndroidTestRule.setContent {
            CurrencyConverterTheme {
                ConverterScreenRoot(
                    state = defaultState,
                    errors = errorFlow,
                    onAmountChanged = { amountField, amount ->
                        wasInvoked = true
                        invokedWithAmount = amount.toDouble()
                        invokedWithIsSource = amountField == AmountField.SOURCE
                    },
                    onCurrencySelected = { _, _ -> },
                    onSwapCurrencies = {}
                )
            }
        }

        with(composeAndroidTestRule) {
            onNodeWithText(sourceAmount.toString()).performClick().performTextInput(newSourceAmountString)
        }

        assert(wasInvoked)
        assert(invokedWithIsSource == true)
        assertEquals(newSourceAmountString.toDouble(), invokedWithAmount)
    }

    @Test
    fun shouldInvokeCalculation_whenTargetAmountChanged() {
        var wasInvoked = false
        var invokedWithAmount: Double? = null
        var invokedWithIsTarget: Boolean? = null
        val newTargetAmountString = "100"

        composeAndroidTestRule.setContent {
            CurrencyConverterTheme {
                ConverterScreenRoot(
                    state = defaultState,
                    errors = errorFlow,
                    onAmountChanged = { amountField, amount ->
                        wasInvoked = true
                        invokedWithAmount = amount.toDouble()
                        invokedWithIsTarget = amountField == AmountField.TARGET
                    },
                    onCurrencySelected = { _, _ -> },
                    onSwapCurrencies = {}
                )
            }
        }

        with(composeAndroidTestRule) {
            onNodeWithText(targetAmount.toString()).performClick().performTextInput(newTargetAmountString)
        }

        assert(wasInvoked)
        assert(invokedWithIsTarget == true)
        assertEquals(newTargetAmountString.toDouble(), invokedWithAmount)
    }

    @Test
    fun shouldInvokeSwapping_whenSwapButtonIsClicked() {
        var wasInvoked = false

        composeAndroidTestRule.setContent {
            CurrencyConverterTheme {
                ConverterScreenRoot(
                    state = defaultState,
                    errors = errorFlow,
                    onAmountChanged = { _, _ -> },
                    onCurrencySelected = { _, _ -> },
                    onSwapCurrencies = { wasInvoked = true }
                )
            }
        }

        with(composeAndroidTestRule) {
            composeAndroidTestRule.onNodeWithContentDescription(activity.getString(R.string.swap_currencies))
                .performClick()
            assert(wasInvoked)
        }
    }

    @Test
    fun shouldChangeCurrencySelection_whenCurrencyFromDropdownClicked() {
        var wasInvoked = false
        var invokedWithSourceCurrency: Boolean? = null
        var invokedWithCorrectCurrency: Boolean? = null
        val newCurrency = CURRENCY_GBP.toCurrencyUi()

        composeAndroidTestRule.setContent {
            CurrencyConverterTheme {
                ConverterScreenRoot(
                    state = defaultState,
                    errors = errorFlow,
                    onAmountChanged = { _, _ -> },
                    onCurrencySelected = { field, currency ->
                        wasInvoked = true
                        invokedWithSourceCurrency = field == AmountField.SOURCE
                        invokedWithCorrectCurrency = currency == newCurrency
                    },
                    onSwapCurrencies = { }
                )
            }
        }

        with(composeAndroidTestRule) {
            onNodeWithText(sourceCurrency.code).performClick()
            onNodeWithText(newCurrency.code).performClick()

            assert(wasInvoked)
            assert(invokedWithCorrectCurrency == true)
            assert(invokedWithSourceCurrency == true)
        }
    }

    @Test
    fun shouldDisplayLoadingSpinner_whenStateIsLoading() {
        composeAndroidTestRule.setContent {
            CurrencyConverterTheme {
                ConverterScreenRoot(
                    state = ConverterState(
                        isLoading = true,
                        currencyList = currencyList,
                        sourceCurrency = sourceCurrency,
                        sourceAmount = sourceAmount,
                        targetCurrency = targetCurrency,
                        targetAmount = targetAmount
                    ),
                    errors = errorFlow,
                    onAmountChanged = { _, _ -> },
                    onCurrencySelected = { _, _ -> },
                    onSwapCurrencies = { }
                )
            }
        }

        with(composeAndroidTestRule) {
            onNodeWithTag(activity.getString(com.sreimler.currencyconverter.core.presentation.R.string.test_tag_styled_progress_indicator)).assertExists()
            onNodeWithText(sourceCurrency.code).assertDoesNotExist()
        }
    }
}
