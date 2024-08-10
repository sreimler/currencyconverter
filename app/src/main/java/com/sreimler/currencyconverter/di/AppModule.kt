package com.sreimler.currencyconverter.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.dsl.module

val appModule = module {
    // Provide Preferences DataStore instance
    single<DataStore<Preferences>> {
        get<Context>().dataStore
    }
}

// Extension property to create DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")
