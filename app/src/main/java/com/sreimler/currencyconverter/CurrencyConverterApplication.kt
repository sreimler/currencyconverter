package com.sreimler.currencyconverter

import android.app.Application
import com.sreimler.currencyconverter.di.appModule
import com.sreimler.currencyconverter.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class CurrencyConverterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@CurrencyConverterApplication)
            androidLogger()
            modules(
                listOf(
                    appModule,
                    viewModelModule
                )
            )
        }
    }
}