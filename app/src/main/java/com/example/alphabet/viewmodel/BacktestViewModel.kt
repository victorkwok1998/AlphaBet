package com.example.alphabet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.alphabet.CostType
import com.example.alphabet.StockStatic
import com.example.alphabet.database.StrategySchema
import com.example.alphabet.getYesterday
import java.util.Calendar

class BacktestViewModel(application: Application): AndroidViewModel(application) {
    val stockList = mutableListOf<StockStatic>()
    val checkedStrategy = mutableMapOf<Long, StrategySchema>()

    private val defaultPeriod = PreferenceManager.getDefaultSharedPreferences(application)
        .getString("strategy_backtest_period", "1")!!
        .toInt()
    val start = MutableLiveData(getYesterday().apply {
        time = getYesterday().time
        add(Calendar.YEAR, -defaultPeriod)
    })
    val end = MutableLiveData(getYesterday())
    val transactionCost = MutableLiveData("0")
    val transactionCostType = MutableLiveData(CostType.BPS)
}