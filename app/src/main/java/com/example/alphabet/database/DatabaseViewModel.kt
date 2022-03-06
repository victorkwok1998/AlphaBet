package com.example.alphabet.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DatabaseViewModel(application: Application): AndroidViewModel(application) {
    val readAllBacktestResultData: LiveData<List<BacktestResultSchema>>
    val readAllStrategy: LiveData<List<StrategySchema>>
    private val repository: BacktestResultRepository

    init {
        val backtestResultDao = MyDatabase.getDatabase(application).backtestResultDao()
        repository = BacktestResultRepository(backtestResultDao)
        readAllBacktestResultData = repository.readAllBacktestResultData
        readAllStrategy = repository.readAllStrategy
    }

    fun addBacktestResult(backtestResultSchema: BacktestResultSchema) {
        viewModelScope.launch {
            repository.addBacktestResult(backtestResultSchema)
        }
    }

    fun deleteBacktestResult(backtestResultSchema: BacktestResultSchema) {
        viewModelScope.launch {
            repository.deleteBacktestResult(backtestResultSchema)
        }
    }

    fun readStrategy(id: Long): LiveData<StrategySchema> {
        return repository.readStrategy(id)
    }

    fun addStrategy(strategy: StrategySchema) {
        viewModelScope.launch {
            repository.addStrategy(strategy)
        }
    }

    fun addPortfolioResult(portfolioResultSchema: PortfolioResultSchema) {
        viewModelScope.launch {
            repository.addPortfolioResult(portfolioResultSchema)
        }
    }
}