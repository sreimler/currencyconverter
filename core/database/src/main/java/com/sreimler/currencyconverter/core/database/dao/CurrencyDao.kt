package com.sreimler.currencyconverter.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sreimler.currencyconverter.core.database.entity.CurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Upsert
    suspend fun upsertCurrency(currency: CurrencyEntity)

    @Upsert
    suspend fun upsertCurrencies(currencies: List<CurrencyEntity>)

    @Query("SELECT * FROM currency ORDER BY code")
    fun observeCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currency WHERE code = :code")
    fun observeCurrency(code: String): Flow<CurrencyEntity?>

    @Query("DELETE FROM currency WHERE code = :code")
    suspend fun deleteCurrency(code: String)

    @Query("DELETE FROM currency")
    suspend fun deleteAllCurrencies()
}