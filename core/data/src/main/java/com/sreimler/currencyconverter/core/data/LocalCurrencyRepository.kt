package com.sreimler.currencyconverter.core.data

import com.sreimler.currencyconverter.core.domain.CURRENCIES_LIST
import com.sreimler.currencyconverter.core.domain.CURRENCY_USD
import com.sreimler.currencyconverter.core.domain.Currency
import com.sreimler.currencyconverter.core.domain.CurrencyRepository
import com.sreimler.currencyconverter.core.domain.EXCHANGE_RATE_LIST
import com.sreimler.currencyconverter.core.domain.ExchangeRate

class LocalCurrencyRepository : CurrencyRepository {
    override suspend fun getCurrencies(): List<Currency> {
        return CURRENCIES_LIST
    }

    override suspend fun getExchangeRates(baseCurrency: Currency): List<ExchangeRate> {
        return EXCHANGE_RATE_LIST
    }

    override fun getBaseCurrency(): Currency {
        return CURRENCY_USD
    }

    override fun setBaseCurrency(currency: Currency) {
        // TODO: implement
    }
}
