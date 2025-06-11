package com.sreimler.currencyconverter.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber


class DataStoreCurrencyStorage(
    private val dataStore: DataStore<Preferences>
) : LocalPreferredCurrencyStorage {

    /**
     * Initializes the datastore with the default value for the base currency
     */
    override suspend fun initialize() {
        val baseCurrency = dataStore.data.map { it[KEY_BASE_CURRENCY] }.firstOrNull()
        if (baseCurrency == null) {
            dataStore.edit { it[KEY_BASE_CURRENCY] = DEFAULT_BASE_CURRENCY }
        }
    }

    /**
     * Stores the symbol of the base currency (e.g. USD) in Preferences DataStore
     */
    override suspend fun setBaseCurrencyCode(currencyCode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_BASE_CURRENCY] = currencyCode
        }
    }

    override suspend fun setConversionSourceCurrency(currencyCode: String) {
        dataStore.edit { preferences ->
            Timber.d("setSourceCurrency: $currencyCode")
            preferences[KEY_SOURCE_CURRENCY] = currencyCode
        }
    }

    override suspend fun setConversionTargetCurrency(currencyCode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_TARGET_CURRENCY] = currencyCode
        }
    }

    /**
     * Retrieves the symbol of the base currency (e.g. USD) from Preferences DataStore
     */
    override suspend fun getBaseCurrencyCode(): Flow<String> {
        return dataStore.data.map { preferences -> preferences[KEY_BASE_CURRENCY] ?: DEFAULT_BASE_CURRENCY }
    }

    override suspend fun getConversionSourceCurrency(): String? {
        return dataStore.data.firstOrNull()?.get(KEY_SOURCE_CURRENCY)
    }

    override suspend fun getConversionTargetCurrency(): String? {
        return dataStore.data.firstOrNull()?.get(KEY_TARGET_CURRENCY)
    }

    companion object {
        private const val DEFAULT_BASE_CURRENCY = "USD"
        private val KEY_BASE_CURRENCY = stringPreferencesKey("KEY_BASE_CURRENCY")
        private val KEY_SOURCE_CURRENCY = stringPreferencesKey("KEY_SOURCE_CURRENCY")
        private val KEY_TARGET_CURRENCY = stringPreferencesKey("KEY_TARGET_CURRENCY")
    }
}