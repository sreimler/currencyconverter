package com.sreimler.currencyconverter.core.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sreimler.currencyconverter.core.data.networking.FreecurrencyApiService
import com.sreimler.currencyconverter.core.data.networking.RetrofitRemoteDataSource
import com.sreimler.currencyconverter.core.domain.RemoteCurrencyDataSource
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit


private const val baseUrl = "https://api.freecurrencyapi.com/v1/"

// Provides the Retrofit instance
val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single {
        get<Retrofit>().create(FreecurrencyApiService::class.java)
    }

    singleOf(::RetrofitRemoteDataSource).bind<RemoteCurrencyDataSource>()
}