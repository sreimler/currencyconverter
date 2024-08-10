package com.sreimler.currencyconverter.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyEntity(
    val symbol: String,
    val name: String,
    val symbolNative: String,
    val decimalDigits: Int,
    val rounding: Int,
    @PrimaryKey val code: String,
    val namePlural: String,
    val type: String,
    val isEnabled: Boolean = true
)
