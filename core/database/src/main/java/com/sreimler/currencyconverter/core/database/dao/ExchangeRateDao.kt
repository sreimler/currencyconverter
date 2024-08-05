package com.sreimler.currencyconverter.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sreimler.currencyconverter.core.database.entity.ExchangeRateEntity
import com.sreimler.currencyconverter.core.database.entity.ExchangeRateWithCurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Insert
    suspend fun insertExchangeRate(exchangeRate: ExchangeRateEntity)

    @Insert
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRateEntity>)

    @Query("SELECT * FROM exchangerate")
    fun getExchangeRatesWithCurrencies(): Flow<List<ExchangeRateWithCurrencyEntity>>

    @Query(
        """
        SELECT * FROM exchangerate 
        GROUP BY baseCurrencySymbol, targetCurrencySymbol 
        HAVING dateTimeUtc = MAX(dateTimeUtc)
        """
    )
    fun getLatestExchangeRatesWithCurrencies(): Flow<List<ExchangeRateWithCurrencyEntity>>

    @Query("SELECT * FROM exchangerate WHERE baseCurrencySymbol = :baseCurrencySymbol AND targetCurrencySymbol = :targetCurrencySymbol ORDER BY dateTimeUtc DESC LIMIT 1")
    fun getLatestExchangeRate(
        baseCurrencySymbol: String,
        targetCurrencySymbol: String
    ): Flow<ExchangeRateWithCurrencyEntity>

    @Query("DELETE FROM exchangerate")
    suspend fun deleteAllExchangeRates()
}