package com.sreimler.currencyconverter.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.sreimler.currencyconverter.core.domain.util.ErrorLogger
import com.sreimler.currencyconverter.util.CrashlyticsErrorLogger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Provide Preferences DataStore instance
    single<DataStore<Preferences>> {
        get<Context>().dataStore
    }

    // Provide crashlytics logger instance
    singleOf(::CrashlyticsErrorLogger).bind<ErrorLogger>()
}

// Extension property to create DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")
