package com.sreimler.currencyconverter.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sreimler.currencyconverter.core.domain.LocalBaseCurrencyStorage
import kotlinx.coroutines.flow.firstOrNull


class DataStoreBaseCurrencyStorage(
    private val dataStore: DataStore<Preferences>
) : LocalBaseCurrencyStorage {

    /**
     * Retrieves the symbol of the base currency (e.g. USD) from Preferences DataStore
     */
    override suspend fun get(): String {
        var baseCurrency = dataStore.data.firstOrNull()?.get(KEY_BASE_CURRENCY)

        // If base currency is not yet set, use default
        if (baseCurrency == null) {
            baseCurrency = DEFAULT_BASE_CURRENCY
            set(baseCurrency)
        }

        return baseCurrency
    }

    /**
     * Stores the symbol of the base currency (e.g. USD) in Preferences DataStore
     */
    override suspend fun set(currencyCode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_BASE_CURRENCY] = currencyCode
        }
    }

    companion object {
        private const val DEFAULT_BASE_CURRENCY = "USD"
        private val KEY_BASE_CURRENCY = stringPreferencesKey("KEY_BASE_CURRENCY")
    }
}