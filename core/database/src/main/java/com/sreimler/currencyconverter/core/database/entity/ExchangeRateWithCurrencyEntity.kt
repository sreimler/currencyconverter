package com.sreimler.currencyconverter.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ExchangeRateWithCurrencyEntity(
    @Embedded val exchangeRateEntity: ExchangeRateEntity,
    @Relation(
        parentColumn = "baseCurrencyCode",
        entityColumn = "code"
    )
    val baseCurrency: CurrencyEntity,
    @Relation(
        parentColumn = "targetCurrencyCode",
        entityColumn = "code"
    )
    val targetCurrency: CurrencyEntity
)
