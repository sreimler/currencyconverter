package com.sreimler.currencyconverter.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sreimler.currencyconverter.core.data.config.SupportedCurrencies.DEFAULT_BASE_CURRENCY
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber


/**
 * Implementation of `LocalPreferredCurrencyStorage` using Android's Preferences DataStore.
 * Provides methods to store and retrieve currency-related preferences.
 *
 * @property dataStore The DataStore instance used for storing preferences.
 */
class DataStoreCurrencyStorage(
    private val dataStore: DataStore<Preferences>
) : LocalPreferredCurrencyStorage {

    override suspend fun initialize(baseCurrenyCode: String) {
        val baseCurrency = dataStore.data.map { it[KEY_BASE_CURRENCY] }.firstOrNull()
        if (baseCurrency == null) {
            setBaseCurrencyCode(baseCurrenyCode)
        }
    }


    override fun getBaseCurrencyCode(): Flow<String> {
        return dataStore.data.map { preferences -> preferences[KEY_BASE_CURRENCY] ?: DEFAULT_BASE_CURRENCY }
    }

    override fun getConversionSourceCurrency(): Flow<String?> {
        return dataStore.data.map { preferences -> preferences[KEY_SOURCE_CURRENCY] }
    }

    override fun getConversionTargetCurrency(): Flow<String?> {
        return dataStore.data.map { preferences -> preferences[KEY_TARGET_CURRENCY] }
    }

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

    companion object {
        private val KEY_BASE_CURRENCY = stringPreferencesKey("KEY_BASE_CURRENCY")
        private val KEY_SOURCE_CURRENCY = stringPreferencesKey("KEY_SOURCE_CURRENCY")
        private val KEY_TARGET_CURRENCY = stringPreferencesKey("KEY_TARGET_CURRENCY")
    }
}