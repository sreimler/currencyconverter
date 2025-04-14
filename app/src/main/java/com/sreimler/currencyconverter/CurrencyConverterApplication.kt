package com.sreimler.currencyconverter

import android.app.Application
import android.util.Log
import com.sreimler.currencyconverter.converter.presentation.di.converterPresentationModule
import com.sreimler.currencyconverter.core.data.di.dataModule
import com.sreimler.currencyconverter.core.data.di.networkModule
import com.sreimler.currencyconverter.core.database.di.databaseModule
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
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    // Include file name, line number, and method name in logs
                    return "(${element.fileName}:${element.lineNumber}) #${element.methodName}"
                }
            })
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.ERROR || priority == Log.WARN) {
                        // TODO: send to crashlytics
                    }
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
                    networkModule,
                    converterPresentationModule
                )
            )
        }
    }
}