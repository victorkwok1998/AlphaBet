package com.example.alphabet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.alphabet.PortfolioInput
import com.example.alphabet.getYesterday
import java.util.Calendar

class PortfolioViewModel(application: Application): AndroidViewModel(application) {
    val symbolWeightingMap = mutableMapOf<String, PortfolioInput>()

    val defaultPeriod = PreferenceManager.getDefaultSharedPreferences(application)
        .getString("portfolio_backtest_period", "1")!!
        .toInt()
    val start = MutableLiveData(getYesterday().apply {
        time = getYesterday().time
        add(Calendar.YEAR, -defaultPeriod)
    })
    val end = MutableLiveData(getYesterday())
}