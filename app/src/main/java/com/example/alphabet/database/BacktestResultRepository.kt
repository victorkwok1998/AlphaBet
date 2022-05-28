package com.example.alphabet.database

import androidx.lifecycle.LiveData
import com.example.alphabet.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BacktestResultRepository(private val backtestResultDao: BacktestResultDao) {
    val readAllBacktestResultData: LiveData<List<BacktestResultSchema>> = backtestResultDao.readAllBacktestResultData()
    val readAllStrategy: LiveData<List<StrategySchema>> = backtestResultDao.readAllStrategy()
    val readAllPortfolioResult: LiveData<List<PortfolioResultSchema>> = backtestResultDao.readAllPortfolioResult()

    suspend fun addBacktestResult(backtestResultSchema: BacktestResultSchema): Long {
        return withContext(Dispatchers.IO) {
            backtestResultDao.addBacktestResult(backtestResultSchema)
        }
    }

    suspend fun deleteBacktestResult(backtestResultSchema: BacktestResultSchema) {
        withContext(Dispatchers.IO) {
            backtestResultDao.deleteBacktestResult(backtestResultSchema)
        }
    }

    fun readStrategy(id: Long): LiveData<StrategySchema> {
        return backtestResultDao.readStrategy(id)
    }

    suspend fun addStrategy(strategy: StrategySchema) {
        withContext(Dispatchers.IO) {
            backtestResultDao.addStrategy(strategy)
        }
    }

    suspend fun addPortfolioResult(portfolioResultSchema: PortfolioResultSchema) {
        withContext(Dispatchers.IO) {
            backtestResultDao.addPortfolioResult(portfolioResultSchema)
        }
    }

    suspend fun deletePortfolioResult(portfolioResultSchema: PortfolioResultSchema) {
        withContext(Dispatchers.IO) {
            backtestResultDao.deletePortfolioResult(portfolioResultSchema)
        }
    }

    suspend fun updatePortfolioResult(portfolioResultSchema: PortfolioResultSchema) {
        withContext(Dispatchers.IO) {
            backtestResultDao.updatePortfolioResult(portfolioResultSchema)
        }
    }

    suspend fun updateStrategy(strategy: StrategySchema) {
        withContext(Dispatchers.IO) {
            backtestResultDao.updateStrategy(strategy)
        }
    }

    suspend fun deleteStrategy(strategy: StrategySchema) {
        withContext(Dispatchers.IO) {
            backtestResultDao.deleteStrategy(strategy)
        }
    }
}