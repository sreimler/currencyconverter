package com.sreimler.currencyconverter.core.data.mappers

import com.sreimler.currencyconverter.core.domain.util.AppResult
import com.sreimler.currencyconverter.core.domain.util.EmptyAppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart


fun <T> Flow<T>.asAppResult(): Flow<AppResult<T>> {
    return this
        .map<T, AppResult<T>> { AppResult.Success(it) }
        .onStart { emit(AppResult.Loading<T>()) }
        .catch { e -> emit(AppResult.Error(e.toAppError())) }
}

fun <T> AppResult<T>.toEmptyResult(): EmptyAppResult = when (this) {
    is AppResult.Success -> AppResult.Success(Unit)
    is AppResult.Error -> AppResult.Error(this.error)
    is AppResult.Loading -> AppResult.Loading()
}

fun <T> AppResult<T>.toString(): String {
    return when (this) {
        is AppResult.Error<*> -> "AppResult.Error(${(this as AppResult.Error).error})"
        is AppResult.Loading<*> -> "AppResult.Loading"
        is AppResult.Success<*> -> "AppResult.Success(${this.data})"
    }
}