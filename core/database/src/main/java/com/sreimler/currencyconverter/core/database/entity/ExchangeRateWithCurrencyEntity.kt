package com.sreimler.currencyconverter.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ExchangeRateWithCurrencyEntity(
    @Embedded val exchangeRateEntity: ExchangeRateEntity,
    @Relation(
        parentColumn = "baseCurrencySymbol",
        entityColumn = "symbol"
    )
    val baseCurrency: CurrencyEntity,
    @Relation(
        parentColumn = "targetCurrencySymbol",
        entityColumn = "symbol"
    )
    val targetCurrency: CurrencyEntity
)
