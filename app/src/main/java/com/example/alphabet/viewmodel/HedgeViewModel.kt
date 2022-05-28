package com.example.alphabet.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alphabet.StockStatic
import com.example.alphabet.database.PortfolioResultSchema

class HedgeViewModel: ViewModel() {
    val hedgePort = MutableLiveData<PortfolioResultSchema?>(null)
    val stock = MutableLiveData<StockStatic?>(null)
}