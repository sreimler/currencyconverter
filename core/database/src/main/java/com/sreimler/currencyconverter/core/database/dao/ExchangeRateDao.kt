package com.sreimler.currencyconverter.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sreimler.currencyconverter.core.database.entity.ExchangeRateEntity
import com.sreimler.currencyconverter.core.database.entity.ExchangeRateWithCurrencyEntity
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) for managing exchange rate data in the database.
 * Provides methods for inserting, querying, and deleting exchange rate records.
 */
@Dao
interface ExchangeRateDao {

    /**
     * Inserts a single exchange rate into the database.
     *
     * @param exchangeRate The `ExchangeRateEntity` to insert.
     */
    @Insert
    suspend fun insertExchangeRate(exchangeRate: ExchangeRateEntity)

    /**
     * Inserts a list of exchange rates into the database.
     *
     * @param exchangeRates The list of `ExchangeRateEntity` objects to insert.
     */
    @Insert
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRateEntity>)

    /**
     * Observes all exchange rates in the database.
     * Returns a flow emitting a list of `ExchangeRateWithCurrencyEntity` objects.
     *
     * @return A `Flow` emitting all exchange rates.
     */
    @Transaction
    @Query("SELECT * FROM exchangerate")
    fun observeAllExchangeRates(): Flow<List<ExchangeRateWithCurrencyEntity>>

    /**
     * Observes the latest exchange rates grouped by target currency code.
     * Returns a flow emitting a list of `ExchangeRateWithCurrencyEntity` objects.
     *
     * @return A `Flow` emitting the latest exchange rates.
     */
    @Transaction
    @Query(
        """
        SELECT * FROM exchangerate
        GROUP BY targetCurrencyCode 
        HAVING dateTimeUtc = MAX(dateTimeUtc)
        """
    )
    fun observeLatestExchangeRates(): Flow<List<ExchangeRateWithCurrencyEntity>>

    /**
     * Observes the latest exchange rate for a specific target currency code.
     * Returns a flow emitting a single `ExchangeRateWithCurrencyEntity` object.
     *
     * @param targetCurrencyCode The target currency code to filter by.
     * @return A `Flow` emitting the latest exchange rate for the specified currency.
     */
    @Transaction
    @Query(
        """
        SELECT * FROM exchangerate 
        WHERE targetCurrencyCode = :targetCurrencyCode 
        ORDER BY dateTimeUtc DESC 
        LIMIT 1
        """
    )
    fun observeLatestExchangeRate(
        targetCurrencyCode: String
    ): Flow<ExchangeRateWithCurrencyEntity>

    /**
     * Deletes all exchange rates from the database.
     */
    @Query("DELETE FROM exchangerate")
    suspend fun deleteAllExchangeRates()
}