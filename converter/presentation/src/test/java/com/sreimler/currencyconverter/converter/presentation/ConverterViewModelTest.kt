package com.sreimler.currencyconverter.converter.presentation

import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_EUR
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_LIST
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_USD
import com.sreimler.currencyconverter.core.domain.mock.ExchangeRateMock.EXCHANGE_RATES
import com.sreimler.currencyconverter.core.domain.util.AppResult
import com.sreimler.currencyconverter.core.domain.util.ErrorLogger
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CurrencyRepository
    private lateinit var logger: ErrorLogger
    private lateinit var viewModel: ConverterViewModel

    // Test-controlled producers
    private var baseFlow = MutableStateFlow<AppResult<Currency>>(AppResult.Success(CURRENCY_USD))
    private var currenciesFlow = MutableStateFlow<AppResult<List<Currency>>>(AppResult.Success(CURRENCY_LIST))
    private var ratesFlow = MutableStateFlow<AppResult<List<ExchangeRate>>>(AppResult.Success(EXCHANGE_RATES))
    private var sourceFlow = MutableStateFlow<AppResult<Currency>>(AppResult.Success(CURRENCY_USD))
    private var targetFlow = MutableStateFlow<AppResult<Currency>>(AppResult.Success(CURRENCY_EUR))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        // Mock flows
        coEvery { repository.baseCurrencyStream() } returns baseFlow
        coEvery { repository.currenciesStream() } returns currenciesFlow
        coEvery { repository.latestExchangeRatesStream() } returns ratesFlow

        coEvery { repository.sourceCurrencyStream() } returns sourceFlow
        coEvery { repository.targetCurrencyStream() } returns targetFlow

        viewModel = ConverterViewModel(repository, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenSwapCurrenciesCalled_thenCurrenciesAreSwapped() = runTest {
        // Let init combine finish
        advanceUntilIdle()

        // Get and verify initial state
        val initial = viewModel.state.value
        assertEquals(CURRENCY_USD.toCurrencyUi(), initial.sourceCurrency)
        assertEquals(CURRENCY_EUR.toCurrencyUi(), initial.targetCurrency)

        viewModel.onSwapCurrencies()

        // Verify swap
        val updated = viewModel.state.value
        assertEquals(CURRENCY_EUR.toCurrencyUi(), updated.sourceCurrency)
        assertEquals(CURRENCY_USD.toCurrencyUi(), updated.targetCurrency)
    }

    @Test
    fun whenSwapCurrenciesCalled_thenAmountsAreRecalculated() = runTest {
        // Let init combine finish
        advanceUntilIdle()

        // Get and verify initial state
        val initial = viewModel.state.value
        val exchangeRate = initial.exchangeRate
        assertEquals(CURRENCY_USD.toCurrencyUi(), initial.sourceCurrency)
        assertEquals(CURRENCY_EUR.toCurrencyUi(), initial.targetCurrency)

        viewModel.onSwapCurrencies()

        // TODO: verify recalculation

        // Verify swap
        val updated = viewModel.state.value
        assertEquals(CURRENCY_EUR.toCurrencyUi(), updated.sourceCurrency)
        assertEquals(CURRENCY_USD.toCurrencyUi(), updated.targetCurrency)
    }
}
