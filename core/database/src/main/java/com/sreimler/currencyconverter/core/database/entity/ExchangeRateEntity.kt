package com.sreimler.currencyconverter.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchangerate")
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val targetCurrencyCode: String,
    val baseCurrencyCode: String,
    val rate: Double,
    val dateTimeUtc: Long
)