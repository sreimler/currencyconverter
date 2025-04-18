package com.sreimler.currencyconverter.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber


class DataStoreCurrencyStorage(
    private val dataStore: DataStore<Preferences>
) : LocalPreferredCurrencyStorage {

    /**
     * Stores the symbol of the base currency (e.g. USD) in Preferences DataStore
     */
    override suspend fun setBaseCurrency(currencyCode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_BASE_CURRENCY] = currencyCode
        }
    }

    override suspend fun setSourceCurrency(currencyCode: String) {
        dataStore.edit { preferences ->
            Timber.d("setSourceCurrency: $currencyCode")
            preferences[KEY_SOURCE_CURRENCY] = currencyCode
        }
    }

    override suspend fun setTargetCurrency(currencyCode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_TARGET_CURRENCY] = currencyCode
        }
    }

    /**
     * Retrieves the symbol of the base currency (e.g. USD) from Preferences DataStore
     */
    override suspend fun getBaseCurrency(): String {
        var baseCurrency = dataStore.data.firstOrNull()?.get(KEY_BASE_CURRENCY)

        // If base currency is not yet set, use default
        if (baseCurrency == null) {
            baseCurrency = DEFAULT_BASE_CURRENCY
            setBaseCurrency(baseCurrency)
        }

        return baseCurrency
    }

    override suspend fun getSourceCurrency(): String? {
        return dataStore.data.firstOrNull()?.get(KEY_SOURCE_CURRENCY)
    }

    override suspend fun getTargetCurrency(): String? {
        return dataStore.data.firstOrNull()?.get(KEY_TARGET_CURRENCY)
    }

    companion object {
        private const val DEFAULT_BASE_CURRENCY = "USD"
        private val KEY_BASE_CURRENCY = stringPreferencesKey("KEY_BASE_CURRENCY")
        private val KEY_SOURCE_CURRENCY = stringPreferencesKey("KEY_SOURCE_CURRENCY")
        private val KEY_TARGET_CURRENCY = stringPreferencesKey("KEY_TARGET_CURRENCY")
    }
}