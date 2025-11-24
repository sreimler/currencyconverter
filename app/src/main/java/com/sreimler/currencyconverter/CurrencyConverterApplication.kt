package com.sreimler.currencyconverter

import android.app.Application
import com.sreimler.currencyconverter.core.data.di.dataModule
import com.sreimler.currencyconverter.core.data.di.networkModule
import com.sreimler.currencyconverter.core.database.di.databaseModule
import com.sreimler.currencyconverter.core.domain.LocalPreferredCurrencyStorage
import com.sreimler.currencyconverter.core.domain.util.ErrorLogger
import com.sreimler.currencyconverter.di.appModule
import com.sreimler.currencyconverter.di.viewModelModule
import com.sreimler.currencyconverter.startup.initializePreferredCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class CurrencyConverterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    // Include file name, line number, and method name in logs
                    return "(${element.fileName}:${element.lineNumber}) #${element.methodName}"
                }
            })
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // No timber logging in production - errors are logged by the ErrorLogger instance
                }
            })
        }

        startKoin {
            androidContext(this@CurrencyConverterApplication)
            androidLogger()
            modules(
                listOf(
                    appModule,
                    viewModelModule,
                    dataModule,
                    databaseModule,
                    networkModule
                )
            )
        }

        // Try to identify and set base currency depending on the device locale
        CoroutineScope(Dispatchers.IO).launch {
            val currencyStorage = getKoin().get<LocalPreferredCurrencyStorage>()
            val errorLogger = getKoin().get<ErrorLogger>()
            initializePreferredCurrency(currencyStorage, errorLogger)
        }
    }
}
