package com.sreimler.currencyconverter.rates.presentation

import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.ExchangeRate
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_EUR
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_LIST
import com.sreimler.currencyconverter.core.domain.mock.CurrencyMock.CURRENCY_USD
import com.sreimler.currencyconverter.core.domain.mock.ExchangeRateMock.EXCHANGE_RATES
import com.sreimler.currencyconverter.core.domain.util.AppResult
import com.sreimler.currencyconverter.core.domain.util.ErrorLogger
import com.sreimler.currencyconverter.core.presentation.models.ExchangeRateUi
import com.sreimler.currencyconverter.core.presentation.models.toCurrencyUi
import com.sreimler.currencyconverter.core.presentation.models.toExchangeRateUi
import io.mockk.coEvery
import io.mockk.coVerify
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
import java.time.ZonedDateTime


@OptIn(ExperimentalCoroutinesApi::class)
class RatesListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CurrencyRepository
    private lateinit var logger: ErrorLogger
    private lateinit var viewModel: RatesListViewModel

    // Test-controlled producers
    private var baseFlow = MutableStateFlow<AppResult<Currency>>(AppResult.Success(CURRENCY_USD))
    private var currenciesFlow = MutableStateFlow<AppResult<List<Currency>>>(AppResult.Success(CURRENCY_LIST))
    private var ratesFlow = MutableStateFlow<AppResult<List<ExchangeRate>>>(AppResult.Success(EXCHANGE_RATES))
    private var lastUpdateFlow =
        MutableStateFlow<AppResult<Long>>(AppResult.Success(ZonedDateTime.now().toEpochSecond()))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        // Mock flows
        coEvery { repository.baseCurrencyStream() } returns baseFlow
        coEvery { repository.currenciesStream() } returns currenciesFlow
        coEvery { repository.latestExchangeRatesStream() } returns ratesFlow
        coEvery { repository.lastUpdateTimeStream() } returns lastUpdateFlow

        viewModel = RatesListViewModel(repository, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenBaseCurrencyMatchesExchangeRateBase_thenUnconvertedRateIsReturned() = runTest {
        // Let init combine finish
        advanceUntilIdle()

        val expectedRate = EXCHANGE_RATES.map { it.toExchangeRateUi() }.getExchangeRate(CURRENCY_USD, CURRENCY_EUR)
        val actualRate = viewModel.state.value.exchangeRates.getExchangeRate(CURRENCY_USD, CURRENCY_EUR)
        assertEquals(expectedRate, actualRate)
    }


    @Test
    fun whenBaseCurrencyDiffersFromExchangeRateBase_thenRateIsCalculatedCorrectly() = runTest {
        // Let init combine finish
        advanceUntilIdle()

        // Verify base currency before - should be USD
        assertEquals(CURRENCY_USD.toCurrencyUi(), viewModel.state.value.baseCurrency)

        // Change base currency to EUR
        viewModel.onCurrencyLongClicked(CURRENCY_EUR.toCurrencyUi())
        advanceUntilIdle()

        // Check if base currency change was sent to the repo
        coVerify(exactly = 1) { repository.setBaseCurrency(CURRENCY_EUR) }

        // From now on, repo should return EUR as base currency
        baseFlow.emit(AppResult.Success(CURRENCY_EUR))
        advanceUntilIdle()

        // Should update the state in the viewmodel
        assertEquals(CURRENCY_EUR.toCurrencyUi(), viewModel.state.value.baseCurrency)

        val usdToEurRate =
            EXCHANGE_RATES.find { it.rateBaseCurrency == CURRENCY_USD && it.currency == CURRENCY_EUR }?.rate
        val expectedEurToUsdRate = 1 / (usdToEurRate ?: 1.0)
        val actualEurToUsdRate =
            requireNotNull(viewModel.state.value.exchangeRates.getExchangeRate(CURRENCY_EUR, CURRENCY_USD)) {
                "Rate should not be null"
            }

        assertEquals(expectedEurToUsdRate, actualEurToUsdRate, 1e-6)
    }


    private fun List<ExchangeRateUi>.getExchangeRate(baseCurrency: Currency, currency: Currency): Double? {
        return this.find { it.baseCurrency == baseCurrency.toCurrencyUi() && it.targetCurrency == currency.toCurrencyUi() }?.rate
    }
}
