package com.example.alphabet.viewmodel

import androidx.lifecycle.ViewModel
import com.example.alphabet.StockStatic
import com.example.alphabet.database.StrategySchema

class BacktestViewModel: ViewModel() {
    val stockList = mutableListOf<StockStatic>()
    val checkedStrategy = mutableMapOf<Long, StrategySchema>()
}