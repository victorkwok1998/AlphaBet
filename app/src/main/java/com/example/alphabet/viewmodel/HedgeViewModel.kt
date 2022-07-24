package com.example.alphabet.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alphabet.StockStatic
import com.example.alphabet.database.PortfolioResultSchema
import org.nield.kotlinstatistics.SimpleRegression

class HedgeViewModel: ViewModel() {
    val hedgePort = MutableLiveData<PortfolioResultSchema?>(null)
    val stock = MutableLiveData<StockStatic?>(null)

    lateinit var regressionData: List<Pair<Float, Float>>
    lateinit var regression: SimpleRegression
}