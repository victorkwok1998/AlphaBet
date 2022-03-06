package com.example.alphabet.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.alphabet.StrategyInput

@Dao
interface BacktestResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBacktestResult(backtestResultSchema: BacktestResultSchema): Long

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addBacktestResultCashFlow(backtestResultCashFlowSchema: List<BacktestResultCashFlowSchema>)

    @Delete
    fun deleteBacktestResult(backtestResultSchema: BacktestResultSchema)
//    @Query("DELETE FROM backtest_result WHERE id = :id")
//    suspend fun deleteBacktestResult(id: Long)


//    @Query("DELETE FROM backtest_result_cash_flow WHERE backtestResultId = :id")
//    suspend fun deleteBacktestResultCashFlow(id: Long)

    @Query("SELECT * FROM backtest_result ORDER BY id ASC")
    fun readAllBacktestResultData(): LiveData<List<BacktestResultSchema>>

//    @Transaction
//    @Query("SELECT * FROM backtest_result")
//    fun readAllBacktestResultData(): LiveData<List<BacktestResultWithCashFlow>>

    @Query("SELECT * FROM strategy WHERE id = :id")
    fun readStrategy(id: Long): LiveData<StrategySchema>

    @Query("SELECT * FROM strategy ORDER BY strategyName ASC")
    fun readAllStrategy(): LiveData<List<StrategySchema>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStrategy(strategy: StrategySchema)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPortfolioResult(portfolioResultSchema: PortfolioResultSchema): Long

}