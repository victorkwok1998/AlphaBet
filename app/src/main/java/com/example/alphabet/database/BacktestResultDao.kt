package com.example.alphabet.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.alphabet.StrategyInput

@Dao
interface BacktestResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBacktestResult(backtestResultSchema: BacktestResultSchema): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBacktestResults(backtestResultSchemaList: List<BacktestResultSchema>): List<Long>

    @Delete
    fun deleteBacktestResult(backtestResultSchema: BacktestResultSchema)

    @Delete
    fun deletePortfolioResult(portfolioResultSchema: PortfolioResultSchema)

    @Delete
    fun deleteStrategy(strategy: StrategySchema)

    @Query("SELECT * FROM backtest_result ORDER BY id ASC")
    fun readAllBacktestResultData(): LiveData<List<BacktestResultSchema>>

    @Query("SELECT * FROM strategy WHERE id = :id")
    fun readStrategy(id: Long): LiveData<StrategySchema>

    @Query("SELECT * FROM strategy ORDER BY strategyName ASC")
    fun readAllStrategy(): LiveData<List<StrategySchema>>

    @Query("SELECT * FROM portfolio_result ORDER BY name ASC")
    fun readAllPortfolioResult(): LiveData<List<PortfolioResultSchema>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStrategy(strategy: StrategySchema)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPortfolioResult(portfolioResultSchema: PortfolioResultSchema): Long

    @Update
    fun updatePortfolioResult(portfolioResultSchema: PortfolioResultSchema)

    @Update
    fun updateStrategy(strategy: StrategySchema)

}