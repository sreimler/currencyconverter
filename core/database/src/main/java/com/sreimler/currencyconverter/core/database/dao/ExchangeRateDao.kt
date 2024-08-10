package com.sreimler.currencyconverter.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sreimler.currencyconverter.core.database.entity.ExchangeRateEntity
import com.sreimler.currencyconverter.core.database.entity.ExchangeRateWithCurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Insert
    suspend fun insertExchangeRate(exchangeRate: ExchangeRateEntity)

    @Insert
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRateEntity>)

    @Transaction
    @Query("SELECT * FROM exchangerate")
    fun getExchangeRatesWithCurrencies(): Flow<List<ExchangeRateWithCurrencyEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM exchangerate 
        WHERE baseCurrencyCode = :baseCurrencyCode
        GROUP BY targetCurrencyCode 
        HAVING dateTimeUtc = MAX(dateTimeUtc)
        """
    )
    fun getLatestExchangeRatesWithCurrencies(baseCurrencyCode: String): Flow<List<ExchangeRateWithCurrencyEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM exchangerate 
        WHERE baseCurrencyCode = :baseCurrencyCode AND targetCurrencyCode = :targetCurrencyCode 
        ORDER BY dateTimeUtc DESC 
        LIMIT 1
        """
    )
    fun getLatestExchangeRate(
        baseCurrencyCode: String,
        targetCurrencyCode: String
    ): Flow<ExchangeRateWithCurrencyEntity>

    @Query("DELETE FROM exchangerate")
    suspend fun deleteAllExchangeRates()
}